package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class BooleanConverter extends AbstractConverter<Boolean> {

	@Override
	public Boolean convert( String value ) throws ConversionException {
		return Boolean.valueOf( value );
	}

}
