package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class ShortConverter extends AbstractConverter<Short> {

	@Override
	public Short convert( String value ) throws ConversionException {
		return Short.valueOf( value );
	}

}
