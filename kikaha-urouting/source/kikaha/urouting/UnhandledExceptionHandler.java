package kikaha.urouting;

import trip.spi.Singleton;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.UnhandledException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UnhandledExceptionHandler implements ExceptionHandler<UnhandledException> {

	@Override
	public Response handle( final UnhandledException exception ) {
		log.error("Unhandled exception", exception);
		return DefaultResponse.serverError( exception.getMessage() );
	}
}
