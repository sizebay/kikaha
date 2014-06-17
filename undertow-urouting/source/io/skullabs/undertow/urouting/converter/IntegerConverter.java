package io.skullabs.undertow.urouting.converter;



public class IntegerConverter extends AbstractConverter<Integer> {

	@Override
	public Integer convert(String value) throws ConversionException {
		return Integer.valueOf( value );
	}

}
