package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;

import java.math.BigDecimal;

import trip.spi.Singleton;

@Singleton( AbstractConverter.class )
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

	@Override
	public BigDecimal convert( String value ) throws ConversionException {
		return new BigDecimal( value );
	}

}
