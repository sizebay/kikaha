package kikaha.core.url;

public class EndOfStringMatcher implements Matcher {

	@Override
	public boolean matches( StringCursor string ) {
		return !string.hasNext();
	}
}
