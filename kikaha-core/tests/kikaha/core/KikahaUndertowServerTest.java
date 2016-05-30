package kikaha.core;

import io.undertow.server.HttpHandler;
import kikaha.core.test.KikahaRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import kikaha.core.test.Exposed;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class KikahaUndertowServerTest {

	@Inject
	KikahaUndertowServer server;

	Exposed exposedServer;

	@Before
	public void initializeMocks() throws Exception {
		server = spy(server);
		doNothing().when(server).start();
		doNothing().when(server).stop();
		server.run();
	}

	@Test
	public void ensureThatHasSetTheDefaultHttpHandler() throws Exception {
		exposedServer = new Exposed(server.server);
		final HttpHandler rootHandler = exposedServer.getFieldValue("rootHandler", HttpHandler.class);
		assertTrue( DefaultHttpRequestHandler.class.equals( rootHandler.getClass() ) );
	}
}
