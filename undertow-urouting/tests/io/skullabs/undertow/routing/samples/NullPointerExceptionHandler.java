package io.skullabs.undertow.routing.samples;

import static org.mockito.Mockito.mock;
import trip.spi.Service;
import urouting.api.ExceptionHandler;
import urouting.api.Response;

@Service
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response handle( NullPointerException exception ) {
		return mock( Response.class );
	}
}
