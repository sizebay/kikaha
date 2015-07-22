package kikaha.core.auth;

import java.util.ArrayList;
import java.util.List;

import kikaha.core.security.AuthenticationMechanism;
import kikaha.core.security.IdentityManager;
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

	// UNCHECKED: this constructor should not be checked
	public AuthenticationRule(
		@NonNull final String pattern,
		@NonNull final List<IdentityManager> identityManager,
		@NonNull final List<AuthenticationMechanism> mechanisms,
		@NonNull final List<String> expectedRoles,
		@NonNull final List<String> exceptionPatterns )
	// CHECKED
	{
		this.pattern = pattern;
		this.matcher = URLMatcher.compile( pattern );
		this.identityManager = identityManager;
		this.mechanisms = mechanisms;
		this.expectedRoles = expectedRoles;
		this.exceptionPatterns = convertToURLMatcher( exceptionPatterns );
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

	public List<URLMatcher> convertToURLMatcher( final List<String> urls ) {
		val matchers = new ArrayList<URLMatcher>();
		for ( val url : urls )
			matchers.add( URLMatcher.compile( url ) );
		return matchers;
	}
}
