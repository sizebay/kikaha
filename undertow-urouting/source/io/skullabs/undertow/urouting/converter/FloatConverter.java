package io.skullabs.undertow.urouting.converter;



public class FloatConverter extends AbstractConverter<Float> {

	@Override
	public Float convert(String value) throws ConversionException {
		return Float.valueOf( value );
	}

}
