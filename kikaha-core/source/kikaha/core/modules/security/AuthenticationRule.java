package kikaha.core.modules.security;

import java.util.List;
import kikaha.core.cdi.helpers.TinyList;
import kikaha.core.url.URLMatcher;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent = true )
@ToString(of = {"pattern", "identityManagers", "mechanisms"})
public class AuthenticationRule {

	private final static String MSG_INVALID_RULE = "Invalid rule: ";

	final String pattern;
	final List<URLMatcher> exceptionPatterns;
	final URLMatcher matcher;
	final List<IdentityManager> identityManagers;
	final List<AuthenticationMechanism> mechanisms;
	final List<String> expectedRoles;

	// UNCHECKED: this constructor should not be checked
	public AuthenticationRule(
		@NonNull final String pattern,
		@NonNull final List<IdentityManager> identityManagers,
		@NonNull final List<AuthenticationMechanism> mechanisms,
		@NonNull final List<String> expectedRoles,
		@NonNull final List<String> exceptionPatterns )
	// CHECKED
	{
		this.pattern = pattern;
		this.matcher = URLMatcher.compile( pattern );
		this.identityManagers = identityManagers;
		this.mechanisms = mechanisms;
		this.expectedRoles = expectedRoles;
		this.exceptionPatterns = convertToURLMatcher( exceptionPatterns );
		if ( isInvalid() )
			throw new UnsupportedOperationException( MSG_INVALID_RULE + toString() );
	}

	private boolean isInvalid(){
		return (identityManagers == null || identityManagers.isEmpty())
			|| (mechanisms == null || mechanisms.isEmpty())
			|| (pattern == null || pattern.isEmpty());
	}

	public boolean matches( final String url ) {
		return matcher.matches( url, null )
			&& !matchesIgnoredUrls( url );
	}

	boolean matchesIgnoredUrls( final String url ) {
		for ( final URLMatcher exceptionPattern : exceptionPatterns )
			if ( exceptionPattern.matches( url, null ) )
				return true;
		return false;
	}

	private List<URLMatcher> convertToURLMatcher( final List<String> urls ) {
		final List<URLMatcher> matchers = new TinyList<>();
		for ( final String url : urls )
			matchers.add( URLMatcher.compile( url ) );
		return matchers;
	}
}
