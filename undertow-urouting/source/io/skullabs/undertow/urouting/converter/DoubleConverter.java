package io.skullabs.undertow.urouting.converter;



public class DoubleConverter extends AbstractConverter<Double> {

	@Override
	public Double convert(String value) throws ConversionException {
		return Double.valueOf( value );
	}

}
