package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class DoubleConverter extends AbstractConverter<Double> {

	@Override
	public Double convert( String value ) throws ConversionException {
		return Double.valueOf( value );
	}

}
