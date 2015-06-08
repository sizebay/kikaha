package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Collection;

import kikaha.core.api.conf.FormAuthConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationRunner implements Runnable {

	final HttpServerExchange exchange;
	final HttpHandler next;
	final SecurityContext context;
	final Collection<String> expectedRoles;
	final FormAuthConfiguration formAuthConfig;

	@Override
	public void run() {
		try {
			context.setAuthenticationRequired();
			if ( context.authenticate() && context.isAuthenticated() ) {
				if ( !exchange.isResponseStarted() )
					tryExecuteChain();
			} else
				endCommunicationWithClient();
		} catch ( final Throwable cause ) {
			handleException( cause );
		}
	}

	void tryExecuteChain() throws Exception {
		if ( matchesExpectedRoles() )
			next.handleRequest( exchange );
		else
			handlePermitionDenied();
	}

	boolean matchesExpectedRoles() {
		int matchedRoles = 0;
		for ( val expectedRole : expectedRoles )
			for ( val role : context.getAuthenticatedAccount().getRoles() )
				if ( expectedRole.equals( role ) )
					matchedRoles++;
		return matchedRoles == expectedRoles.size();
	}

	void handlePermitionDenied() {
		if ( !exchange.isResponseStarted() )
			if ( formAuthConfig.permitionDeniedPage().isEmpty() )
				sendForbidenError();
			else
				redirectToPermitionDeniedPage();
		endCommunicationWithClient();
	}

	void sendForbidenError() {
		exchange.setResponseCode( 403 );
		exchange.getResponseSender().send( "Permition Denied" );
	}

	void redirectToPermitionDeniedPage() {
		exchange.setResponseCode( 303 );
		exchange.getResponseHeaders().put( Headers.LOCATION, formAuthConfig.permitionDeniedPage() );
	}

	void handleException( final Throwable cause ) {
		cause.printStackTrace();
		if ( !exchange.isResponseStarted() ) {
			exchange.setResponseCode( 500 );
			exchange.getResponseSender().send( "Internal Server Error: " + cause.getMessage() );
		}
		exchange.endExchange();
	}

	void endCommunicationWithClient() {
		exchange.endExchange();
	}
}
