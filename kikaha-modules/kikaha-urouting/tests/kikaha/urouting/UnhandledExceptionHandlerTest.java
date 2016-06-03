package kikaha.urouting;

import static org.junit.Assert.assertEquals;
import kikaha.urouting.api.Response;
import org.junit.Test;

/**
 * Unit tests for {@link UnhandledExceptionHandler}.
 */
public class UnhandledExceptionHandlerTest {

	private static final String ERROR_MSG = "ERROR_MSG";

	@Test
	public void handleExceptions() throws Exception {
		final UnhandledExceptionHandler handler = new UnhandledExceptionHandler();
		final Response response = handler.handle(new RuntimeException(ERROR_MSG));
		assertEquals( 500, response.statusCode(), 0 );
		assertEquals( ERROR_MSG, response.entity() );
	}

	@Test
	public void handleNullPointerException() throws Exception {
		final UnhandledExceptionHandler handler = new UnhandledExceptionHandler();
		final Response response = handler.handle(new NullPointerException());
		assertEquals( 500, response.statusCode(), 0 );
		assertEquals( "NullPointerException", response.entity() );
	}
}