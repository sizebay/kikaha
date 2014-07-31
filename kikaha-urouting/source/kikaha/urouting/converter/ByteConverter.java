package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;
import trip.spi.Singleton;

@Singleton( exposedAs = AbstractConverter.class )
public class ByteConverter extends AbstractConverter<Byte> {

	@Override
	public Byte convert( String value ) throws ConversionException {
		return Byte.valueOf( value );
	}

}
