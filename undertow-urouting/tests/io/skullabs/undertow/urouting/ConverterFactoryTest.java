package io.skullabs.undertow.urouting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.skullabs.undertow.urouting.api.ConversionException;
import io.skullabs.undertow.urouting.api.ConverterFactory;

import org.junit.Test;

import trip.spi.Provided;

public class ConverterFactoryTest extends TestCase {

	@Provided
	ConverterFactory factory;

	@Test
	public void grantThatCouldConvertSomeDataToString() throws ConversionException {
		assertThat( convertTo( "1.6", Double.class ), is( 1.6D ) );
		assertThat( convertTo( "true", Boolean.class ), is( true ) );
	}

	public <T> T convertTo( String value, Class<T> targetClass ) throws ConversionException {
		return factory.decode( value, targetClass );
	}
}
