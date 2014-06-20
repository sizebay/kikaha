package io.skullabs.undertow.routing;

import static org.junit.Assert.assertNotNull;
import io.skullabs.undertow.urouting.RoutingMethodExceptionHandler;
import org.junit.Test;
import trip.spi.Provided;
import urouting.api.Response;

public class RoutingMethodExceptionHandlerTest extends TestCase {

	@Provided
	RoutingMethodExceptionHandler handler;

	@Test
	public void grantThatHandleNullPointerException() {
		Throwable nullPointerException = new NullPointerException();
		Response handledResponse = handler.handle( nullPointerException );
		assertNotNull( handledResponse );
	}
}
