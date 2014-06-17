package io.skullabs.undertow.urouting.converter;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractConverter<T> {

	public abstract T convert( String value ) throws ConversionException;

	@SuppressWarnings( "unchecked" )
	public Class<T> getGenericClass() {
		return (Class<T>)( (ParameterizedType)getClass().getGenericSuperclass() )
				.getActualTypeArguments()[0];
	}
}