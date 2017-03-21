package kikaha.urouting.unit;

import kikaha.core.cdi.ApplicationRunner;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A test to run Undertow server.
 */
@Ignore
public class UndertowTest {

	@Test
	@SneakyThrows
	public void runUndertow() {
		ApplicationRunner.main( null );
	}
}
