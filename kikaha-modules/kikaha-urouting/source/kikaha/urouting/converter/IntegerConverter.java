package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed( AbstractConverter.class )
public class IntegerConverter extends AbstractConverter<Integer> {

	@Override
	public Integer convert( String value ) throws ConversionException {
		return Integer.valueOf( value );
	}

}
