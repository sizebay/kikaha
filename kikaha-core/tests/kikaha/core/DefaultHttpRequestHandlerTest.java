package kikaha.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.KikahaException;
import kikaha.core.impl.DefaultHttpRequestHandler;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class DefaultHttpRequestHandlerTest {

	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();

	@Mock
	DeploymentContext context;

	@Mock
	HttpHandler rootHandler;

	@Test
	@SneakyThrows
	public void ensureThatHaveAttachedDefaultSessionManagerAndConfigIntoExchange() {
		ensureThatDelegateRequestToMockedRequestHook();

		val sessionManager = exchange.getAttachment( SessionManager.ATTACHMENT_KEY );
		assertNotNull( sessionManager );
		assertThat( sessionManager, instanceOf( InMemorySessionManager.class ) );

		val sessionConfig = exchange.getAttachment( SessionConfig.ATTACHMENT_KEY );
		assertNotNull( sessionConfig );
		assertThat( sessionConfig, instanceOf( SessionCookieConfig.class ) );
	}

	@Test
	public void ensureThatDelegateRequestToMockedRequestHook() throws Exception, KikahaException {
		doReturn( rootHandler ).when( context ).rootHandler();
		val handler = new DefaultHttpRequestHandler( context );
		handler.handleRequest( exchange );
		verify( rootHandler ).handleRequest(eq( exchange ));
	}
}
