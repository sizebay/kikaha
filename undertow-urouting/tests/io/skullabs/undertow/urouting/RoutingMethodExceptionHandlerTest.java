package io.skullabs.undertow.urouting;

import static org.junit.Assert.assertNotNull;
import io.skullabs.undertow.urouting.RoutingMethodExceptionHandler;
import io.skullabs.undertow.urouting.api.Response;

import org.junit.Test;

import trip.spi.Provided;

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
