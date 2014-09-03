package kikaha.core.auth;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionManager;
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
		setEmptyUndertowSessionManagerOnExchange( exchange );
		setSecurityContextForThisExchange( exchange, context );
		populateWithAuthenticationMechanisms( context, rule );
		registerNotificationReceivers( context, rule );
		return context;
	}

	void setEmptyUndertowSessionManagerOnExchange( HttpServerExchange exchange ) {
		exchange.putAttachment( SessionManager.ATTACHMENT_KEY, new EmptyUndertowSessionManager() );
	}

	void setSecurityContextForThisExchange( HttpServerExchange exchange, final SecurityContext context ) {
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
