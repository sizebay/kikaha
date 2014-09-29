package kikaha.core.url;

import java.util.Map;

public interface Matcher {

	boolean matches( final StringCursor string, final Map<String, String> foundParameters );
}
