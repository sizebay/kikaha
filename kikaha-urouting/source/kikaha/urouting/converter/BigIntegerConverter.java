package kikaha.urouting.converter;

import java.math.BigInteger;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class BigIntegerConverter extends AbstractConverter<BigInteger> {

	@Override
	public BigInteger convert( String value ) throws ConversionException {
		return new BigInteger( value );
	}

}
