package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;
import io.skullabs.undertow.urouting.api.ConversionException;

import java.math.BigInteger;

import trip.spi.Singleton;

@Singleton( AbstractConverter.class )
public class BigIntegerConverter extends AbstractConverter<BigInteger> {

	@Override
	public BigInteger convert( String value ) throws ConversionException {
		return new BigInteger( value );
	}

}
