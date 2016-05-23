package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kikaha.core.cdi.ProviderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class KeyValueProviderContext implements ProviderContext {

	final Map<Class<?>, Annotation> annotationMap = new HashMap<>();
	final Map<Object, Object> attributes;
	Class<?> targetType;

	public KeyValueProviderContext() {
		attributes = new HashMap<>();
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <A extends Annotation> A getAnnotation( Class<A> anntationClass ) {
		return (A)annotationMap.get( anntationClass );
	}

	public <A extends Annotation> void setAnnotation( Class<A> anntationClass, A annotation ) {
		annotationMap.put( anntationClass, annotation );
	}

	public void attribute( Object key, Object value ) {
		attributes.put( key, value );
	}

	public <T> void attribute( Class<T> key, T value ) {
		attributes.put( key.getCanonicalName(), value );
	}

	@Override
	public Object attribute( Object key ) {
		return attributes.get( key );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T attribute( Class<T> key ) {
		return (T)attribute( key.getCanonicalName() );
	}

	@Override
	public Collection<Class<? extends Annotation>> qualifierAnnotations() {
		return Collections.emptyList();
	}
}
