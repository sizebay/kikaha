package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class IntegerConverter extends AbstractConverter<Integer> {

	@Override
	public Integer convert( String value ) throws ConversionException {
		return Integer.valueOf( value );
	}

}
