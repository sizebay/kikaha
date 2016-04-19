package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed( AbstractConverter.class )
public class LongConverter extends AbstractConverter<Long> {

	@Override
	public Long convert( String value ) throws ConversionException {
		return Long.valueOf( value );
	}

}
