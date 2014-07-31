package kikaha.urouting.converter;

import java.math.BigDecimal;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

	@Override
	public BigDecimal convert( String value ) throws ConversionException {
		return new BigDecimal( value );
	}

}
