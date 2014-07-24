package io.skullabs.undertow.standalone.url;

public class EndOfStringMatcher implements Matcher {

	@Override
	public boolean matches( StringCursor string ) {
		return !string.hasNext();
	}
}
