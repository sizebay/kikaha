package kikaha.urouting;

import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.ConversionException;
import kikaha.urouting.api.ConverterFactory;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith( KikahaRunner.class )
public class ConverterFactoryTest {

	@Inject
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
