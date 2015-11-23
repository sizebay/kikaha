package kikaha.urouting;

import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Singleton;

@Slf4j
@Singleton
public class UnhandledExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public Response handle( final Throwable exception ) {
		log.error("Unhandled exception", exception);
		return DefaultResponse.serverError( exception.getMessage() );
	}
}
