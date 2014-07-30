package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class IntegerConverter extends AbstractConverter<Integer> {

	@Override
	public Integer convert( String value ) throws ConversionException {
		return Integer.valueOf( value );
	}

}
