package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import kikaha.core.url.URLMatcher;
import lombok.val;

public class PathPredicate implements Function<HttpServerExchange, Map<String, String>> {

	final URLMatcher expectedPath;

	public PathPredicate( final String path ) {
		expectedPath = URLMatcher.compile( path );
	}

	@Override
	public Map<String, String> apply( final HttpServerExchange exchange ) {
		val relativePath = exchange.getRelativePath();
		val properties = new HashMap<String, String>();
		if ( expectedPath.matches( relativePath, properties ) )
			return properties;
		return null;
	}
}
