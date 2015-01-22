package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;

import java.util.Map;
import java.util.function.Function;

import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import kikaha.core.url.URLMatcher;
import lombok.val;

public class RewriteRequestHook implements RequestHook {

	final Function<HttpServerExchange, Map<String, String>> predicate;
	final URLMatcher path;
	final URLMatcher targetPath;

	public RewriteRequestHook(
		final Function<HttpServerExchange, Map<String, String>> predicate,
		final String path,
		final String targetPath )
	{
		this.predicate = predicate;
		this.path = URLMatcher.compile( path );
		this.targetPath = URLMatcher.compile( targetPath );
	}

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange )
		throws UndertowStandaloneException
	{
		val properties = predicate.apply( exchange );
		if ( properties != null && path.matches( exchange.getRelativePath(), properties ) )
			exchange.setRelativePath( targetPath.replace( properties ) );
		chain.executeNext();
	}
}

