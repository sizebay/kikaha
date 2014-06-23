package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Service;

@Service( AbstractConverter.class )
public class ShortConverter extends AbstractConverter<Short> {

	@Override
	public Short convert( String value ) throws ConversionException {
		return Short.valueOf( value );
	}

}
