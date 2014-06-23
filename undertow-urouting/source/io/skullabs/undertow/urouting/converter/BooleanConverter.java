package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Service;

@Service( AbstractConverter.class )
public class BooleanConverter extends AbstractConverter<Boolean> {

	@Override
	public Boolean convert( String value ) throws ConversionException {
		return Boolean.valueOf( value );
	}

}
