package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.enterprise.inject.Typed;

import kikaha.core.cdi.DefaultServiceProvider.DependencyInjector;
import kikaha.core.cdi.helpers.filter.Condition;
import kikaha.core.cdi.helpers.filter.QualifierCondition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings( { "unchecked" } )
public class ManyElementsProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;

	@Override
	public void provide( Object instance, DependencyInjector provider ) throws Throwable {
		final Object value = provider.loadAll( fieldType, condition );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	public static <T> ProvidableField from( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		assertFieldTypeIsIterable( field );
		field.setAccessible( true );
		final Typed Inject = field.getAnnotation( Typed.class );
		if ( Inject == null )
			throw new IllegalStateException( "Field " + field.getName()
					+ " (annotated with @Any) also expects to be annotated with @Typed." );
		return new ManyElementsProvidableField<T>(
				field, (Class<T>)Inject.value()[0],
				(Condition<T>)new QualifierCondition<>( qualifiers ) );
	}

	private static void assertFieldTypeIsIterable( final Field field ) {
		if ( !Iterable.class.equals( field.getType() ) )
			throw new IllegalStateException( "Field " + field.getName() + " expects to have Iterable type." );
	}
}
