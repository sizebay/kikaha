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
		context.setAuthenticationRequired();
		if ( context.authenticate() && matchesExpectedRoles() )
			tryExecuteChain();
	}

	boolean matchesExpectedRoles() {
		int matchedRoles = 0;
		for ( String expectedRole : expectedRoles )
			for ( String role : context.getAuthenticatedAccount().getRoles() )
				if ( expectedRole.equals( role ) )
					matchedRoles++;
		return matchedRoles == expectedRoles.size();
	}

	void tryExecuteChain() {
		try {
			chain.executeNext();
		} catch ( UndertowStandaloneException e ) {
			handleException( e );
		}
	}

	void handleException( UndertowStandaloneException e ) {
		e.printStackTrace();
		val exchange = chain.exchange();
		if ( !exchange.isResponseStarted() )
			exchange.setResponseCode( 500 );
		exchange.endExchange();
	}
}
