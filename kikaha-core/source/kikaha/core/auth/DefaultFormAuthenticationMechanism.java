package kikaha.core.auth;

import static io.undertow.util.StatusCodes.FOUND;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.Sessions;
import kikaha.core.api.conf.AuthenticationRuleConfiguration;
import kikaha.core.api.conf.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.Provided;

public class DefaultFormAuthenticationMechanism implements AuthenticationMechanismFactory {

	@Provided
	Configuration configuration;

	@Override
	public AuthenticationMechanism create( final AuthenticationRuleConfiguration rule ) {
		val config = configuration.authentication().formAuth();
		val postLocation = rule.pattern().replaceAll( "\\*$", "" ) + config.postLocation();
		return new FixedFormAuthenticationMechanism(
			config.name(), config.loginPage(), config.errorPage(), postLocation );
	}
}

class FixedFormAuthenticationMechanism extends FormAuthenticationMechanism {

	final static String AUTHENTICATED_ACCOUNT = "ALREADY_AUTHENTICATED_BEFORE";

	public FixedFormAuthenticationMechanism(
		final String name, final String loginPage,
		final String errorPage, final String postLocation )
	{
		super( name, loginPage, errorPage, postLocation );
	}

	@Override
	public AuthenticationMechanismOutcome authenticate( final HttpServerExchange exchange, final SecurityContext securityContext ) {
		final Session session = Sessions.getSession( exchange );
		if ( session != null && userWasPreviouslyAuthenticated( session ) )
			return notifyAlreadyAuthenticated( session, securityContext );
		return super.authenticate( exchange, securityContext );
	}

	AuthenticationMechanismOutcome notifyAlreadyAuthenticated( final Session session, final SecurityContext securityContext ) {
		final Account account = (Account)session.getAttribute( AUTHENTICATED_ACCOUNT );
		securityContext.authenticationComplete( account, "Cached", true );
		return AuthenticationMechanismOutcome.AUTHENTICATED;
	}

	boolean userWasPreviouslyAuthenticated( final Session session ) {
		return session.getAttribute( AUTHENTICATED_ACCOUNT ) != null;
	}

	/**
	 * This is just a fixed version of
	 * {@link FormAuthenticationMechanism#handleRedirectBack}
	 */
	@Override
	protected void handleRedirectBack( final HttpServerExchange exchange ) {
		final Session session = Sessions.getSession( exchange );
		if ( session != null ) {
			notifyUserWasPreviouslyAuthenticatedBefore( session, exchange );
			final String location = (String)session.removeAttribute( LOCATION_ATTRIBUTE );
			if ( location != null )
				exchange.addDefaultResponseListener( new HandlerToRedirectUserBackToOriginalLocation( location ) );
		}
	}

	void notifyUserWasPreviouslyAuthenticatedBefore( final Session session, final HttpServerExchange exchange ) {
		final Account authenticatedAccount = exchange.getSecurityContext().getAuthenticatedAccount();
		session.setAttribute( AUTHENTICATED_ACCOUNT, authenticatedAccount );
	}

	@RequiredArgsConstructor
	class HandlerToRedirectUserBackToOriginalLocation implements DefaultResponseListener {

		final String location;

		@Override
		public boolean handleDefaultResponse( final HttpServerExchange exchange ) {
			final HeaderMap responseHeaders = exchange.getResponseHeaders();
			responseHeaders.put( Headers.LOCATION, location );
			exchange.setResponseCode( FOUND );
			exchange.endExchange();
			return true;
		}
	}
}