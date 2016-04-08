package tests;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

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
}