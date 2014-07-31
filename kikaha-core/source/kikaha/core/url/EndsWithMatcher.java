package kikaha.core.url;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class EndsWithMatcher implements Matcher {

	final char[] patternChars;

	@Override
	public boolean matches( StringCursor string ) {
		val pattern = new StringCursor( patternChars );
		char firstPatternChar = pattern.next();
		return string.shiftCursorToNextChar( firstPatternChar )
				&& pattern.matches( string );
	}
}
