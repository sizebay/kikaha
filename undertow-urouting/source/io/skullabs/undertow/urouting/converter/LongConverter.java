package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import trip.spi.Service;

@Service( AbstractConverter.class )
public class LongConverter extends AbstractConverter<Long> {

	@Override
	public Long convert( String value ) throws ConversionException {
		return Long.valueOf( value );
	}

}
