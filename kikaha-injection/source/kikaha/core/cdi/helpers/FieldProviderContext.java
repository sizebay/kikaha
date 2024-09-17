package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import kikaha.core.cdi.ProviderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Holds data about a field some value should be injected.
 */
@Getter
@Accessors(fluent=true)
@RequiredArgsConstructor
public class FieldProviderContext implements ProviderContext {

	final Collection<Class<? extends Annotation>> qualifierAnnotations;
	final Field field;

	@Override
	public <A extends Annotation> A getAnnotation( Class<A> annotationClass ) {
		return field.getAnnotation( annotationClass );
	}

	@Override
	public Class<?> targetType() {
		return field.getType();
	}

	@Override
	public Object attribute( Object key ) {
		return null;
	}

	@Override
	public <T> T attribute( Class<T> key ) {
		return null;
	}

	@Override
	public String toString() {
		return field.toString();
	}
}
