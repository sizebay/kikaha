package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import javax.enterprise.inject.Typed;
import kikaha.core.cdi.DefaultCDI.DependencyInjector;
import kikaha.core.cdi.*;
import kikaha.core.cdi.helpers.filter.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@SuppressWarnings( { "unchecked", "rawtypes" } )
public class SingleElementProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;
	final ProviderContext providerContext;

	@Override
	public void provide( Object instance, DependencyInjector provider )
			throws ServiceProviderException, IllegalArgumentException, IllegalAccessException
	{
		final Object value = provider.load( fieldType, condition, providerContext );
		if ( value == null )
			log.warn( "No data found for " + fieldType.getCanonicalName()
					+ "; Location: " + instance.getClass().getCanonicalName()
					+ "; Condition: " + condition );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	@Override
	public String toString() {
		return field.toString();
	}

	public static <T> ProvidableField from( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		field.setAccessible( true );
		final Typed Inject = field.getAnnotation( Typed.class );
		final Class expectedClass = Inject != null
				? Inject.value()[0]
				: field.getType();
		return new SingleElementProvidableField<>(
				field, (Class<T>)expectedClass,
				createInjectionCondition( qualifiers, field ),
				new FieldProviderContext( qualifiers, field ) );
	}

	private static <T> Condition<T> createInjectionCondition( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		final ChainedCondition<T> condition = new ChainedCondition<>();
		condition.add( (Condition<T>)new IsAssignableFrom( field.getType() ) );
		condition.add( new QualifierCondition<>( qualifiers ) );
		return condition;
	}
}
