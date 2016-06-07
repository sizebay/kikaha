package kikaha.core.modules.websocket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import java.io.IOException;
import javax.inject.Inject;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.core.CloseMessage;
import kikaha.config.*;
import kikaha.core.DeploymentContext;
import kikaha.core.cdi.*;
import kikaha.core.modules.http.WebResource;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketModuleTest {

	@Mock
	DeploymentContext context;

	@Inject
	WebSocketModule module;

	@Inject
	WebSocketPlainTextSerializers plainTextSerializers;

	@Before
	@SneakyThrows
	public void setup() {
		final ServiceProvider provider = new DefaultServiceProvider();
		provider.providerFor( Config.class, ConfigLoader.loadDefaults() );
		provider.providerFor( WebSocketHandler.class, new MyFirstWebSocket() );
		provider.provideOn( this );
	}

	@Test
	public void shouldInjectTheDefaultSerializer(){
		assertEquals( plainTextSerializers, module.serializer );
		assertEquals( plainTextSerializers, module.unserializer );
	}

	@Test
	public void ensureThatDeployedMyFirstWebSocket() {
		module.load( null, context );
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