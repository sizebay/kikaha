package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import kikaha.core.cdi.Provided;
import kikaha.core.cdi.ServiceProviderException;
import kikaha.core.cdi.helpers.filter.Condition;
import lombok.Value;
import lombok.extern.java.Log;
import kikaha.core.cdi.DefaultServiceProvider.DependencyInjector;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.helpers.filter.ChainedCondition;
import kikaha.core.cdi.helpers.filter.IsAssignableFrom;
import kikaha.core.cdi.helpers.filter.QualifierCondition;

@Log
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
			log.warning( "No data found for " + fieldType.getCanonicalName() + ". Condition: " + condition );
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
		final Provided provided = field.getAnnotation( Provided.class );
		final Class expectedClass = provided == null || provided.exposedAs().equals( Provided.class )
			? field.getType() : provided.exposedAs();
		return new SingleElementProvidableField<T>(
			field, (Class<T>)expectedClass,
				createInjectionCondition( qualifiers, field),
				new FieldProviderContext( qualifiers, field ) );
	}

	private static <T> Condition<T> createInjectionCondition(Collection<Class<? extends Annotation>> qualifiers, final Field field) {
		final ChainedCondition<T> condition = new ChainedCondition<>();
		condition.add((Condition<T>)new IsAssignableFrom( field.getType() ));
		condition.add((Condition<T>)new QualifierCondition<>(qualifiers));
		return condition;
	}
}
