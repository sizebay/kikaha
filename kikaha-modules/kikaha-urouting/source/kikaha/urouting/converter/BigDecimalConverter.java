package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
@Typed(  AbstractConverter.class )
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

	@Override
	public BigDecimal convert( String value ) throws ConversionException {
		return new BigDecimal( value );
	}

}
