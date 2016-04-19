package kikaha.urouting;

import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.Response;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith( KikahaRunner.class )
public class RoutingMethodExceptionHandlerTest {

	@Inject
	RoutingMethodExceptionHandler handler;

	@Test
	public void grantThatHandleNullPointerException() {
		final Throwable nullPointerException = new NullPointerException();
		final Response handledResponse = handler.handle( nullPointerException );
		assertNotNull( handledResponse );
	}
}
