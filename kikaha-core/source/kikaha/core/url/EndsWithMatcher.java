package kikaha.core.url;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class EndsWithMatcher implements Matcher {

	final char[] patternChars;

	@Override
	public boolean matches( StringCursor string , Map<String, String> foundParameters  ) {
		val pattern = new StringCursor( patternChars );
		char firstPatternChar = pattern.next();
		return string.shiftCursorToNextChar( firstPatternChar )
				&& pattern.matches( string );
	}
}
