package io.skullabs.undertow.standalone.auth;

import io.skullabs.undertow.standalone.url.URLMatcher;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.idm.IdentityManager;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@RequiredArgsConstructor
public class AuthenticationRule {

	final String pattern;
	final IdentityManager identityManager;
	final List<AuthenticationMechanism> mechanisms;
	final List<String> expectedRoles;
	final NotificationReceiver notificationReceiver;

	@Getter( lazy = true )
	private final URLMatcher matcher = URLMatcher.compile( pattern() );

	public boolean matches( String url ) {
		return matcher().matches( url );
	}

	public boolean isThereSomeoneListeningForAuthenticationEvents() {
		return notificationReceiver != null;
	}
}
