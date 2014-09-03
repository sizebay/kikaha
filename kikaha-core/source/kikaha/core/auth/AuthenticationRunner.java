package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.impl.SecurityContextFactoryImpl;

import java.util.Collection;

import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class AuthenticationRunner implements Runnable {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory contextFactory = SecurityContextFactoryImpl.INSTANCE;

	final SecurityContext context;
	final RequestHookChain chain;
	final Collection<String> expectedRoles;

	@Override
	public void run() {
		try {
			context.setAuthenticationRequired();
			if ( context.authenticate() )
				tryExecuteChain();
			else
				handleAuthenticationRequired();
		} catch ( Throwable cause ) {
			handleException( cause );
		}
	}

	boolean matchesExpectedRoles() {
		int matchedRoles = 0;
		for ( val expectedRole : expectedRoles )
			for ( val role : context.getAuthenticatedAccount().getRoles() )
				if ( expectedRole.equals( role ) )
					matchedRoles++;
		return matchedRoles == expectedRoles.size();
	}

	void tryExecuteChain() throws UndertowStandaloneException {
		if ( matchesExpectedRoles() )
			chain.executeNext();
		else
			handlePermitionDenied();
	}

	void handleAuthenticationRequired() {
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() ) {
			exchange.setResponseCode( 401 );
			exchange.getResponseSender().send( "Authentication Required" );
		}
		exchange.endExchange();
	}

	void handlePermitionDenied() {
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() ) {
			exchange.setResponseCode( 403 );
			exchange.getResponseSender().send( "Permition Denied" );
		}
		exchange.endExchange();
	}

	void handleException( Throwable cause ) {
		cause.printStackTrace();
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() ) {
			exchange.setResponseCode( 500 );
			exchange.getResponseSender().send( "Internal Server Error: " + cause.getMessage() );
		}
		exchange.endExchange();
	}
}
