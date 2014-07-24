package io.skullabs.undertow.standalone.url;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class EqualsMatcher implements Matcher {

	final char[] patternChars;

	@Override
	public boolean matches( StringCursor string ) {
		val pattern = new StringCursor( patternChars );
		return pattern.matches( string );
	}
}
