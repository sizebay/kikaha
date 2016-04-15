package kikaha.core.modules.websocket;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.core.CloseMessage;

import java.io.IOException;

import kikaha.config.Config;
import kikaha.config.ConfigLoader;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.cdi.ServiceProvider;
import kikaha.core.modules.http.WebResource;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketDeploymentHookTest {

	@Mock
	DeploymentContext context;

	@Inject
	WebSocketDeploymentHook deploymentHook;

	@Before
	@SneakyThrows
	public void setup() {
		final ServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Config.class, ConfigLoader.loadDefaults() );
		provider.providerFor( WebSocketHandler.class, new MyFirstWebSocket() );
		provider.provideOn( this );
	}

	@Test
	public void ensureThatDeployedMyFirstWebSocket() {
		deploymentHook.load( null, context );
		verify( context ).register(
			eq( "/my-first-websocket" ), eq( "GET" ), any( HttpHandler.class ) );
	}
}

@WebResource( path = "/my-first-websocket" )
class MyFirstWebSocket implements WebSocketHandler {

	@Override
	public void onOpen( final WebSocketSession session ) {
	}

	@Override
	public void onText( final WebSocketSession session, final String message ) throws IOException {
	}

	@Override
	public void onClose( final WebSocketSession session, final CloseMessage cm ) {
	}
}