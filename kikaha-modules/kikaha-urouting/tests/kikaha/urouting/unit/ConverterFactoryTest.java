package kikaha.urouting.unit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( KikahaRunner.class )
public class ConverterFactoryTest {

	@Inject
	ConverterFactory factory;

	@Test
	public void grantThatCouldConvertSomeDataFromString() throws ConversionException {
		assertThat( convertTo( "1.6", Double.class ), is( 1.6D ) );
		assertThat( convertTo( "true", Boolean.class ), is( true ) );
	}

	@Test
	public void grantThatCouldConvertStringFromPrimitives() throws ConversionException {
		assertThat( convertTo( "1.6", Double.TYPE ), is( 1.6D ) );
		assertThat( convertTo( "true", Boolean.TYPE ), is( true ) );
	}

	public <T> T convertTo( String value, Class<T> targetClass ) throws ConversionException {
		return factory.decode( value, targetClass );
	}
}
