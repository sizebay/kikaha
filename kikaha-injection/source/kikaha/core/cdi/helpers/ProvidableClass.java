package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import kikaha.core.cdi.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Stores all meta data extracted from an injectable class.
 */
@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class ProvidableClass<T> {

	final Class<T> targetClazz;
	final Iterable<ProvidableField> fields;
	final Consumer<Object> postConstructor;

	public static <T> ProvidableClass<T> wrap(InjectableDataExtractor extractor, Class<T> targetClazz )
	{
		return new ProvidableClass<T>(
				targetClazz,
				readClassProvidableFields( extractor, targetClazz ),
				readPostConstructor( targetClazz ) );
	}

	static Consumer<Object> readPostConstructor( Class<?> targetClazz )
	{
		Method postConstructor = null;
		for ( final Method method : targetClazz.getMethods() )
			if ( method.isAnnotationPresent( PostConstruct.class ) ) {
				postConstructor = method;
				break;
			}
		return postConstructor != null
				? new PostConstructorMethod( postConstructor )
				: EmptyMethod.INSTANCE;
	}

	static Iterable<ProvidableField> readClassProvidableFields(InjectableDataExtractor extractor, Class<?> targetClazz )
	{
		final List<ProvidableField> providableFields = new TinyList<>();
		Class<? extends Object> clazz = targetClazz;
		while ( !Object.class.equals( clazz ) ) {
			populateWithProvidableFields( extractor, clazz, providableFields );
			if ( clazz.isAnnotationPresent( GeneratedFromStatelessService.class ) )
				break;
			clazz = clazz.getSuperclass();
		}
		return providableFields;
	}

	static void populateWithProvidableFields(InjectableDataExtractor extractor, Class<?> targetClazz, List<ProvidableField> providableFields ) {
		for ( final Field field : targetClazz.getDeclaredFields() ) {
			final Collection<Class<? extends Annotation>> qualifiers = extractQualifiersAvoidingNPEWhenCreatingQualifierExtractorFrom( extractor, field );
			if ( extractor.isAManyElementsProvider( field ) )
				providableFields.add( ManyElementsProvidableField.from( qualifiers, field ) );
			else if ( extractor.isASingleElementProvider( field ) )
				providableFields.add( SingleElementProvidableField.from( qualifiers, field ) );
		}
	}

	private static Collection<Class<? extends Annotation>>
        extractQualifiersAvoidingNPEWhenCreatingQualifierExtractorFrom(
			final InjectableDataExtractor extractor, final Field field )
	{
		if ( null == extractor )
			return Collections.emptyList();
		return extractor.extractQualifiersFrom( field );
	}
}

class EmptyMethod implements Consumer<Object> {

	static final Consumer<Object> INSTANCE = new EmptyMethod();

	@Override
	public void accept( Object t ) {}
}

@RequiredArgsConstructor
class PostConstructorMethod implements Consumer<Object> {

	final Method method;

	@Override
	public void accept( Object target ) {
		try {
			method.invoke( target );
		} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new ServiceProviderException( "Can't call " + method.toString(), e );
		}
	}
}