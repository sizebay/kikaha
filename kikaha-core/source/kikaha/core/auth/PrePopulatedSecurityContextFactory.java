package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor( staticName = "wrap" )
public class PrePopulatedSecurityContextFactory
		implements SecurityContextFactory {

	final SecurityContextHandler securityContextHandler = SecurityContextHandler.DEFAULT;
	final SecurityContextFactory wrapped;

	@Override
	public SecurityContext createSecurityContextFor( HttpServerExchange exchange, AuthenticationRule rule ) {
		val context = wrapped.createSecurityContextFor( exchange, rule );
		setSecurityContextForThisExchange( exchange, context );
		populateWithAuthenticationMechanisms( context, rule );
		registerNotificationReceivers( context, rule );
		return context;
	}

	void setSecurityContextForThisExchange( HttpServerExchange exchange, final io.undertow.security.api.SecurityContext context ) {
		securityContextHandler.setSecurityContext( exchange, context );
	}

	void populateWithAuthenticationMechanisms(
			final SecurityContext context, AuthenticationRule rule ) {
		for ( val authenticationMechanism : rule.mechanisms() )
			context.addAuthenticationMechanism( authenticationMechanism );
	}

	void registerNotificationReceivers(
			final SecurityContext context, AuthenticationRule rule ) {
		if ( rule.isThereSomeoneListeningForAuthenticationEvents() )
			context.registerNotificationReceiver( rule.notificationReceiver() );
	}
}
