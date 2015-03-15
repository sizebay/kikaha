package kikaha.urouting;

import trip.spi.Singleton;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;
import kikaha.urouting.api.UnhandledException;

@Singleton
public class UnhandledExceptionHandler implements ExceptionHandler<UnhandledException> {

	@Override
	public Response handle( UnhandledException exception ) {
		exception.printStackTrace();
		return DefaultResponse.serverError( exception.getMessage() );
	}
}
