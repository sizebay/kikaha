package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class StringConverter extends AbstractConverter<String> {

	@Override
	public String convert( String value ) throws ConversionException {
		return value;
	}
}
