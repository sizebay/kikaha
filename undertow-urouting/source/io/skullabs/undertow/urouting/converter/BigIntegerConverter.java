package io.skullabs.undertow.urouting.converter;

import io.skullabs.undertow.urouting.api.AbstractConverter;

import java.math.BigInteger;

import trip.spi.Service;

@Service( AbstractConverter.class )
public class BigIntegerConverter extends AbstractConverter<BigInteger> {

	@Override
	public BigInteger convert( String value ) throws ConversionException {
		return new BigInteger( value );
	}

}
