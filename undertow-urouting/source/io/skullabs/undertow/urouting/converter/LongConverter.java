package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( AbstractConverter.class )
public class LongConverter extends AbstractConverter<Long> {

	@Override
	public Long convert( String value ) throws ConversionException {
		return Long.valueOf( value );
	}

}
