package kikaha.core.websocket;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.core.CloseMessage;

import java.io.IOException;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentListener;
import kikaha.core.api.WebResource;
import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;
import trip.spi.ServiceProvider;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketDeploymentHookTest {

	@Mock
	DeploymentContext context;

	@Provided( exposedAs = DeploymentListener.class )
	WebSocketDeploymentHook deploymentHook;

	@Before
	@SneakyThrows
	public void setup() {
		final ServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Configuration.class, DefaultConfiguration.loadDefaultConfiguration() );
		provider.providerFor( WebSocketHandler.class, new MyFirstWebSocket() );
		provider.provideOn( this );
	}

	@Test
	public void ensureThatDeployedMyFirstWebSocket() {
		deploymentHook.onDeploy( context );
		verify( context ).register(
			eq( "/my-first-websocket" ), eq( "GET" ), any( HttpHandler.class ) );
	}
}

@WebResource( "/my-first-websocket" )
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