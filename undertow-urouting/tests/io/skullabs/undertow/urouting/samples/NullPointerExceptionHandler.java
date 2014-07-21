package io.skullabs.undertow.urouting.samples;

import static org.mockito.Mockito.mock;
import io.skullabs.undertow.urouting.api.ExceptionHandler;
import io.skullabs.undertow.urouting.api.Response;
import trip.spi.Singleton;

@Singleton
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response handle( NullPointerException exception ) {
		return mock( Response.class );
	}
}
