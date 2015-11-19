package kikaha.urouting;

import static org.junit.Assert.assertNotNull;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.RoutingMethodExceptionHandler;
import kikaha.urouting.api.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import trip.spi.Provided;

@RunWith( KikahaRunner.class )
public class RoutingMethodExceptionHandlerTest {

	@Provided
	RoutingMethodExceptionHandler handler;

	@Test
	public void grantThatHandleNullPointerException() {
		final Throwable nullPointerException = new NullPointerException();
		final Response handledResponse = handler.handle( nullPointerException );
		assertNotNull( handledResponse );
	}
}
