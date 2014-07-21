package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( AbstractConverter.class )
public class ByteConverter extends AbstractConverter<Byte> {

	@Override
	public Byte convert( String value ) throws ConversionException {
		return Byte.valueOf( value );
	}

}
