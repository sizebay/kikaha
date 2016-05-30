package kikaha.core.test;

import java.lang.reflect.Field;
import lombok.*;

/**
 * Machiavellian code created to be used only on unit-tests. It helps us to bypass
 * some odds from Undertow API through the Java Reflection API.
 * <p>
 * Don't use under production environments: not secure, not thread-safe and not that fast too.
 */
@RequiredArgsConstructor
public class Exposed {

	final Object object;

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public <T> T getFieldValue( String name, Class<T> target ) {
		final Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return (T)field.get(object);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public void setFieldValue( String name, Object value ) {
		final Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(object, value);
	}
}