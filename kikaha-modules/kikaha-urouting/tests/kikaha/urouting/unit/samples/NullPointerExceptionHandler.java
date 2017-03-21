package kikaha.urouting.unit.samples;

import static org.mockito.Mockito.mock;

import kikaha.urouting.api.*;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import java.io.*;

@Singleton
@Typed( ExceptionHandler.class )
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response handle( NullPointerException exception ) {
		final StringWriter writer = new StringWriter();
		exception.printStackTrace( new PrintWriter( writer ) );
		return DefaultResponse.serverError( writer.toString() );
	}
}
