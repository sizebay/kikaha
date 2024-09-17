package kikaha.core.modules.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.http.HttpHandlerDeploymentModule.HttpHandlerDeploymentCustomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpHandlerDeploymentModuleTest {

	@Spy HttpHandlerDeploymentModule module;
	@Mock DeploymentContext context;
	@Mock HttpHandler customizedHandler;

	@Test
	public void ensureThatHaveDeployedAllHttpHandlers() throws IOException {
		final MyHandler handler = new MyHandler();
		module.handlers = Arrays.asList( handler );
		module.customizers = Arrays.asList();
		module.load( null, context );

		verify( context ).register( eq("/path"), eq("POST"), eq(handler) );
	}

	@Test
	public void ensureThatCanDeployCustomizedHandler() throws IOException {
		final HttpHandlerDeploymentCustomizer customizer = (h, w) -> customizedHandler;

		final MyHandler handler = new MyHandler();
		module.handlers = Arrays.asList( handler );
		module.customizers = Arrays.asList( customizer );
		module.load( null, context );

		verify( context ).register( eq("/path"), eq("POST"), eq(customizedHandler) );
	}

	@Test
	public void ensureThatCanDeployHandlersEvenIfCustomizersReturnsNull() throws IOException {
		final HttpHandlerDeploymentCustomizer customizer = mock( HttpHandlerDeploymentCustomizer.class );
		doReturn( null ).when( customizer ).customize( any(), any() );

		final MyHandler handler = new MyHandler();
		module.handlers = Arrays.asList( handler );
		module.customizers = Arrays.asList( customizer );
		module.load( null, context );

		verify( context ).register( eq("/path"), eq("POST"), eq(handler) );
	}
}

@WebResource(path="/path",method = "POST")
class MyHandler implements HttpHandler {
	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
	}
}