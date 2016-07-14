package kikaha.core.url;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceHolderForAnyStringUntilEndMatcher implements Matcher {

	private static final char SLASH = '/';

	final String placeholder;
	final boolean doNotIgnoreSlashes;

	@Override
	public boolean matches( final StringCursor string , final Map<String, String> foundParameters  ) {
		boolean matched = false;

		if ( !doNotIgnoreSlashes || !string.shiftCursorToNextChar( SLASH ) ) {
			string.mark();
			string.end();
			foundParameters.put(placeholder, string.substringFromLastMark());
			matched = true;
		}

		return matched;
	}

	@Override
	public void replace( final StringBuilder buffer , final Map<String, String> foundParameters  ) {
		if ( foundParameters.containsKey( placeholder ) )
			buffer.append( foundParameters.get( placeholder ) );
	}

	@Override
	public String toString() {
		return "PlaceHolder( " + placeholder + ": *" + (doNotIgnoreSlashes ? "/" : "") + " )";
	}
}
