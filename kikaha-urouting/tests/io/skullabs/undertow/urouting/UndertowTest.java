package io.skullabs.undertow.urouting;

import io.skullabs.undertow.standalone.Main;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;

import java.io.IOException;

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
