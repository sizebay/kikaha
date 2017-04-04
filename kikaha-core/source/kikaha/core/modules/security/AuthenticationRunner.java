package kikaha.core.modules.security;

import java.util.Collection;
import io.undertow.server.*;
import io.undertow.util.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationRunner implements Runnable {

	final HttpServerExchange exchange;
	final HttpHandler next;
	final SecurityContext context;
	final Collection<String> expectedRoles;
	final String permissionDeniedPage;

	@Override
	public void run() {
		try {
			if ( !context.isAuthenticationRequired() || (context.authenticate() && context.isAuthenticated()) ) {
				if ( !exchange.isResponseStarted() )
					tryExecuteChain();
			} else
				endCommunicationWithClient();
		// UNCHECKED: It really should handle all exceptions here
		} catch ( final Throwable cause ) {
		// CHECKED
			handleException( cause );
		}
	}

	void tryExecuteChain() throws Exception {
		if ( !context.isAuthenticated() || matchesExpectedRoles() )
			next.handleRequest(exchange);
		else
			handlePermissionDenied();
	}

	boolean matchesExpectedRoles() {
		int matchedRoles = 0;
		for ( String expectedRole : expectedRoles )
			for ( String role : context.getAuthenticatedAccount().getRoles() )
				if ( expectedRole.equals( role ) )
					matchedRoles++;
		return matchedRoles == expectedRoles.size();
	}

	void handlePermissionDenied() {
		if ( !exchange.isResponseStarted() )
			if ( permissionDeniedPage == null || permissionDeniedPage.isEmpty() )
				sendForbiddenError();
			else
				redirectToPermissionDeniedPage();
		endCommunicationWithClient();
	}

	void sendForbiddenError() {
		exchange.setStatusCode( 403 );
		exchange.getResponseSender().send( "Permission Denied" );
	}

	void redirectToPermissionDeniedPage() {
		exchange.setStatusCode( 303 );
		exchange.getResponseHeaders().put( Headers.LOCATION, permissionDeniedPage );
	}

	void handleException( final Throwable cause ) {
		log.error( "Failed to execute the endpoint", cause );
		if ( !exchange.isResponseStarted() ) {
			exchange.setStatusCode( 500 );
			exchange.getResponseSender().send( "Internal Server Error: " + cause.getMessage() );
		}
		exchange.endExchange();
	}

	void endCommunicationWithClient() {
		exchange.endExchange();
	}
}
