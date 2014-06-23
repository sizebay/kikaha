package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Service;

@Service( AbstractConverter.class )
public class StringConverter extends AbstractConverter<String> {

	@Override
	public String convert( String value ) throws ConversionException {
		return value;
	}
}
