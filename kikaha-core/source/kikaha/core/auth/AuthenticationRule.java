package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.idm.IdentityManager;

import java.util.List;

import kikaha.core.url.URLMatcher;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
public class AuthenticationRule {

	final URLMatcher matcher;
	final IdentityManager identityManager;
	final List<AuthenticationMechanism> mechanisms;
	final List<String> expectedRoles;
	final NotificationReceiver notificationReceiver;
	final SecurityContextFactory securityContextFactory;

	public AuthenticationRule(
			final String pattern, final IdentityManager identityManager,
			final List<AuthenticationMechanism> mechanisms,
			final List<String> expectedRoles,
			final NotificationReceiver notificationReceiver,
			final SecurityContextFactory securityContextFactory ) {
		this.matcher = URLMatcher.compile( pattern );
		this.identityManager = identityManager;
		this.mechanisms = mechanisms;
		this.expectedRoles = expectedRoles;
		this.notificationReceiver = notificationReceiver;
		this.securityContextFactory = PrePopulatedSecurityContextFactory
				.wrap( securityContextFactory );
	}

	public boolean matches( String url ) {
		return matcher.matches( url );
	}

	public boolean isThereSomeoneListeningForAuthenticationEvents() {
		return notificationReceiver != null;
	}
}
