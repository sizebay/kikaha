package kikaha.core.modules.smart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.inject.Inject;
import java.io.IOException;
import io.undertow.server.*;
import kikaha.core.DeploymentContext;
import kikaha.core.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for StaticHeaderModule.
 */
@RunWith( KikahaRunner.class )
public class StaticHeaderModuleTest {

	@Inject StaticHeaderModule module;

	@Test
	public void ensureThatIsAbleToSendHeaders() throws IOException {
		final HttpHandler handler = mock( HttpHandler.class );
		final DeploymentContext deploymentContext = new DeploymentContext();
		deploymentContext.rootHandler( handler );

		module.load( null, deploymentContext );

		final HttpHandler httpHandler = deploymentContext.rootHandler();
		final StaticHeadersHttpHandler staticHeadersHttpHandler = (StaticHeadersHttpHandler)httpHandler;
		assertEquals( 1, staticHeadersHttpHandler.headers.size() );
		assertEquals( handler, staticHeadersHttpHandler.nextHandler );
		assertNotNull( staticHeadersHttpHandler.matcher );
	}
}