package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import trip.spi.Service;

@Service( AbstractConverter.class )
public class FloatConverter extends AbstractConverter<Float> {

	@Override
	public Float convert( String value ) throws ConversionException {
		return Float.valueOf( value );
	}

}
