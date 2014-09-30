package kikaha.core.impl;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import kikaha.core.api.DeploymentContext;
import kikaha.core.api.RequestHookChain;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {

	final SessionManager sessionManager = new InMemorySessionManager( "" );
	final SessionConfig sessionConfig = createSessionConfig();
	final DeploymentContext context;

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
		attachSessionConfigAndManagerInto( exchange );
		final RequestHookChain chain = new DefaultRequestHookChain( exchange, context );
		chain.executeNext();
	}

	private void attachSessionConfigAndManagerInto( final HttpServerExchange exchange ) {
		exchange.putAttachment( SessionManager.ATTACHMENT_KEY, sessionManager );
		exchange.putAttachment( SessionConfig.ATTACHMENT_KEY, sessionConfig );
	}

	private SessionCookieConfig createSessionConfig() {
		final SessionCookieConfig config = new SessionCookieConfig();
		config.setCookieName( "SESSIONID" );
		return config;
	}
}
