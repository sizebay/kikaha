package kikaha.core.impl;

import kikaha.core.api.DeploymentContext;
import kikaha.core.url.URL;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHttpRequestHandler implements HttpHandler {

	final SessionManager sessionManager = new InMemorySessionManager( "" );
	final SessionConfig sessionConfig = createSessionConfig();
	final DeploymentContext context;

	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
		attachSessionConfigAndManagerInto( exchange );
		fixRelativePath( exchange );
		context.rootHandler().handleRequest(exchange);
	}

	void attachSessionConfigAndManagerInto( final HttpServerExchange exchange ) {
		exchange.putAttachment( SessionManager.ATTACHMENT_KEY, sessionManager );
		exchange.putAttachment( SessionConfig.ATTACHMENT_KEY, sessionConfig );
	}

	void fixRelativePath( final HttpServerExchange exchange ) {
		final String relativePath = URL.removeTrailingCharacter( exchange.getRelativePath() );
		exchange.setRelativePath( relativePath );
	}

	SessionCookieConfig createSessionConfig() {
		final SessionCookieConfig config = new SessionCookieConfig();
		config.setCookieName( "SESSIONID" );
		return config;
	}
}
