package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.util.Headers;

import java.util.Collection;

import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
import kikaha.core.api.conf.FormAuthConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationRunner implements Runnable {

	final SecurityContext context;
	final RequestHookChain chain;
	final Collection<String> expectedRoles;
	final FormAuthConfiguration formAuthConfig;

	@Override
	public void run() {
		try {
			context.setAuthenticationRequired();
			if ( context.authenticate() && context.isAuthenticated() ) {
				if ( !chain.exchange().isResponseStarted() )
					tryExecuteChain();
			} else
				endCommunicationWithClient();
		} catch ( final Throwable cause ) {
			handleException( cause );
		}
	}

	void tryExecuteChain() throws KikahaException {
		if ( matchesExpectedRoles() )
			chain.executeNext();
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
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() )
			if ( formAuthConfig.permitionDeniedPage().isEmpty() )
				sendForbidenError( exchange );
			else
				redirectToPermitionDeniedPage( exchange );
		endCommunicationWithClient();
	}

	void sendForbidenError( final io.undertow.server.HttpServerExchange exchange ) {
		exchange.setResponseCode( 403 );
		exchange.getResponseSender().send( "Permition Denied" );
	}

	void redirectToPermitionDeniedPage( final io.undertow.server.HttpServerExchange exchange ) {
		exchange.setResponseCode( 303 );
		exchange.getResponseHeaders().put( Headers.LOCATION, formAuthConfig.permitionDeniedPage() );
	}

	void handleException( final Throwable cause ) {
		cause.printStackTrace();
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() ) {
			exchange.setResponseCode( 500 );
			exchange.getResponseSender().send( "Internal Server Error: " + cause.getMessage() );
		}
		exchange.endExchange();
	}

	void endCommunicationWithClient() {
		chain.exchange().endExchange();
	}
}
