package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( AbstractConverter.class )
public class DoubleConverter extends AbstractConverter<Double> {

	@Override
	public Double convert( String value ) throws ConversionException {
		return Double.valueOf( value );
	}

}
