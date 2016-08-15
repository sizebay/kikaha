package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.enterprise.inject.Typed;

import kikaha.core.cdi.DefaultServiceProvider.DependencyInjector;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.helpers.filter.Condition;
import kikaha.core.cdi.helpers.filter.QualifierCondition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings( { "unchecked" } )
public class ManyElementsProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;
	final ProviderContext providerContext;

	@Override
	public void provide( Object instance, DependencyInjector provider ) throws Throwable {
		final Object value = provider.loadAll( fieldType, condition, providerContext );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	public static <T> ProvidableField from( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		assertFieldTypeIsIterable( field );
		field.setAccessible( true );
		final Class collectionType = identifyWhichTypeThisCollectionHas(field);
		return new ManyElementsProvidableField<>(
				field, (Class<T>)collectionType,
				new QualifierCondition<>( qualifiers ),
				new FieldProviderContext( qualifiers, field ) );
	}

	private static void assertFieldTypeIsIterable( final Field field ) {
		if ( !DefaultFieldQualifierExtractor.fieldRepresentsACollection( field ) )
			throw new IllegalStateException( "Field " + field.getName() + " expects to have Iterable type." );
	}

	private static Class identifyWhichTypeThisCollectionHas( final Field field ){
		final Typed annotation = field.getAnnotation( Typed.class );
		return annotation.value()[0];
	}
}
