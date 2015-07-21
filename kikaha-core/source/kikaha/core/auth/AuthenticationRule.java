package kikaha.core.auth;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.idm.IdentityManager;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.url.URLMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
public class AuthenticationRule {

	final String pattern;
	final List<URLMatcher> exceptionPatterns;
	final URLMatcher matcher;
	final List<IdentityManager> identityManager;
	final List<AuthenticationMechanism> mechanisms;
	final List<String> expectedRoles;
	final NotificationReceiver notificationReceiver;
	final SecurityContextFactory securityContextFactory;

	// UNCHECKED: this constructor should not be checked
	public AuthenticationRule(
		@NonNull final String pattern,
		@NonNull final List<IdentityManager> identityManager,
		@NonNull final List<AuthenticationMechanism> mechanisms,
		@NonNull final List<String> expectedRoles,
		final NotificationReceiver notificationReceiver,
		@NonNull final SecurityContextFactory securityContextFactory,
		@NonNull final List<String> exceptionPatterns )
	// CHECKED
	{
		this.pattern = pattern;
		this.matcher = URLMatcher.compile( pattern );
		this.identityManager = identityManager;
		this.mechanisms = mechanisms;
		this.expectedRoles = expectedRoles;
		this.notificationReceiver = notificationReceiver;
		this.exceptionPatterns = convertToURLMatcher( exceptionPatterns );
		this.securityContextFactory = PrePopulatedSecurityContextFactory
				.wrap( securityContextFactory );
	}

	public boolean matches( final String url ) {
		return matcher.matches( url, null )
			&& !matchesIgnoredUrls( url );
	}

	boolean matchesIgnoredUrls( final String url ) {
		for ( val exceptionPattern : exceptionPatterns )
			if ( exceptionPattern.matches( url, null ) )
				return true;
		return false;
	}

	public boolean isThereSomeoneListeningForAuthenticationEvents() {
		return notificationReceiver != null;
	}

	public List<URLMatcher> convertToURLMatcher( final List<String> urls ) {
		val matchers = new ArrayList<URLMatcher>();
		for ( val url : urls )
			matchers.add( URLMatcher.compile( url ) );
		return matchers;
	}
}
