package kikaha.urouting.converter;

import kikaha.urouting.api.AbstractConverter;
import kikaha.urouting.api.ConversionException;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed(  AbstractConverter.class )
public class BooleanConverter extends AbstractConverter<Boolean> {

	@Override
	public Boolean convert( String value ) throws ConversionException {
		return Boolean.valueOf( value );
	}

}
