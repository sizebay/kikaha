package kikaha.core.url;

import java.util.Map;

public class EndOfStringMatcher implements Matcher {

	@Override
	public boolean matches( StringCursor string , Map<String, String> foundParameters  ) {
		return !string.hasNext();
	}
}
