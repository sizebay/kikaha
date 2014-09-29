package kikaha.core.url;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceHolderMatcher implements Matcher {

	static final int GAP_BETWEEN_END_OF_PLACEHOLDER_AND_NEXT_CHAR = 1;
	final String placeholder;
	final char nextChar;

	@Override
	public boolean matches( final StringCursor string , final Map<String, String> foundParameters  ) {
		final boolean existsNextChar = string.shiftCursorToNextChar( nextChar );
		if ( existsNextChar )
			foundParameters.put( placeholder, string.substringFromLastMark( GAP_BETWEEN_END_OF_PLACEHOLDER_AND_NEXT_CHAR ) );
		return existsNextChar;
	}
}
