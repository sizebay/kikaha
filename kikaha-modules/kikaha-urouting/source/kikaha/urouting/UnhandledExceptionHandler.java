package kikaha.urouting;

import javax.inject.Singleton;
import kikaha.urouting.api.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UnhandledExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public Response handle( final Throwable exception ) {
		log.error("Unhandled exception", exception);
		String msg = NullPointerException.class.equals(exception.getClass())
				? "NullPointerException" : exception.getMessage();
		return DefaultResponse.serverError( msg );
	}
}
