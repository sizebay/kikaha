package kikaha.core.modules.smart;

import static org.junit.Assert.*;
import java.io.IOException;
import javax.inject.*;
import io.undertow.server.HttpHandler;
import kikaha.core.*;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@Singleton
@RunWith(KikahaRunner.class)
public class CORSFilterModuleTest {

	final DeploymentContext context = new DeploymentContext();

	@Inject
	CORSFilterModule module;

	@Inject
	NotFoundHandler notFoundHandler;

	@Test
	public void ensureThatHaveDeployedTheCORSModule() throws IOException {
		module.load( null, context );
		assertNotNull( context.rootHandler() );
		assertEquals( CORSFilterHttpHandler.class, context.rootHandler().getClass() );
	}

	@Test
	public void ensureThatDeployedModulePointsToOriginalRootHandlerAsNextHandlerInTheChain() throws IOException {
		final HttpHandler originalHandler = context.rootHandler();
		module.load( null, context );

		final CORSFilterHttpHandler newHandler = (CORSFilterHttpHandler)context.rootHandler();
		assertEquals( originalHandler, newHandler.next );
	}

	@Test
	public void ensureThatDeployedModulePointsToNotFoundHandlerAsFailureHandler() throws IOException {
		module.load( null, context );

		final CORSFilterHttpHandler newHandler = (CORSFilterHttpHandler)context.rootHandler();
		assertEquals( notFoundHandler, newHandler.notFound );
	}

	@Test
	public void ensureThatDeployedModuleHaveReadGETAndPOSTAsAllowedMethodsFromDefaultConfig() throws IOException {
		module.load( null, context );

		final CORSFilterHttpHandler newHandler = (CORSFilterHttpHandler)context.rootHandler();
		assertEquals( 1, newHandler.config.allowedMethods.size() );
		assertTrue( newHandler.config.allowedMethods.contains("GET") );
	}

	@Test
	public void ensureThatDeployedModuleHaveReadValidOriginsFromConfig() throws IOException {
		module.load( null, context );

		final CORSFilterHttpHandler newHandler = (CORSFilterHttpHandler)context.rootHandler();
		assertEquals( 2, newHandler.config.allowedOriginMatchers.size() );
	}
}
