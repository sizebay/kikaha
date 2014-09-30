package kikaha.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
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
	RequestHook requestHook;

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
	public void ensureThatDelegateRequestToMockedRequestHook() throws Exception, UndertowStandaloneException {
		doReturn( createHookChain() ).when( context ).requestHooks();
		val handler = new DefaultHttpRequestHandler( context );
		handler.handleRequest( exchange );
		verify( requestHook ).execute( any( RequestHookChain.class ), eq( exchange ) );
	}

	List<RequestHook> createHookChain() {
		val chain = new ArrayList<RequestHook>();
		chain.add( requestHook );
		return chain;
	}
}
