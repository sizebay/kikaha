package kikaha.core.url;

import java.util.Map;

public class AnyStringUntilEndMatcher implements Matcher {

	@Override
	public boolean matches( final StringCursor string , final Map<String, String> foundParameters  ) {
		string.end();
		return true;
	}
}
