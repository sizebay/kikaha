package kikaha.urouting;

import java.io.IOException;

import kikaha.core.Main;
import kikaha.core.api.UndertowStandaloneException;

import org.junit.Ignore;
import org.junit.Test;

/**
 * A test to run Undertow server.
 */
@Ignore
public class UndertowTest {

	@Test
	public void runUndertow() throws ClassNotFoundException, InterruptedException, UndertowStandaloneException, IOException {
		Main.main( null );
	}
}
