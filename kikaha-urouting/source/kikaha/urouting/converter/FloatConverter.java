package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class FloatConverter extends AbstractConverter<Float> {

	@Override
	public Float convert( String value ) throws ConversionException {
		return Float.valueOf( value );
	}

}
