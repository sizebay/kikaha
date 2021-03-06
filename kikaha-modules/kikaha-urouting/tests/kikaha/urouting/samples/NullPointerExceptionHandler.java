package kikaha.urouting.samples;

import static org.mockito.Mockito.mock;
import kikaha.urouting.api.ExceptionHandler;
import kikaha.urouting.api.Response;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed( ExceptionHandler.class )
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response handle( NullPointerException exception ) {
		return mock( Response.class );
	}
}
