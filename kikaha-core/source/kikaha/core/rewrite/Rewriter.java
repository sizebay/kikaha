package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import kikaha.core.api.conf.RewritableRule;
import kikaha.core.url.StringCursor;
import kikaha.core.url.URLMatcher;
import lombok.val;

public class Rewriter {

	static final EmptyConsumer EMPTYCALLBACK = new EmptyConsumer();
	final List<BiFunction<HttpServerExchange, Map<String, String>, Boolean>> rewriteRules = createWriteRules();

	final URLMatcher virtualHost;
	final URLMatcher path;
	final URLMatcher targetPath;

	public Rewriter( final RewritableRule route ) {
		this(
			route.virtualHost(),
			route.path(),
			route.target() );
	}

	public Rewriter(
		final String virtualHost,
		final String path,
		final String targetPath )
	{
		this.virtualHost = URLMatcher.compile( virtualHost );
		this.path = URLMatcher.compile( path );
		this.targetPath = URLMatcher.compile( targetPath );
	}

	public void rewrite( final HttpServerExchange exchange ) {
		rewrite( exchange, EMPTYCALLBACK, EMPTYCALLBACK );
	}

	public void rewrite( final HttpServerExchange exchange,
		final Consumer<HttpServerExchange> callback,
		final Consumer<HttpServerExchange> failureCallback )
	{
		val properties = new HashMap<String, String>();
		for ( val rule : rewriteRules )
			if ( !rule.apply( exchange, properties ) ) {
				failureCallback.accept( exchange );
				return;
			}
		val newpath = targetPath.replace( properties );
		exchange.setRelativePath( newpath );
		callback.accept( exchange );
	}

	List<BiFunction<HttpServerExchange, Map<String, String>, Boolean>> createWriteRules()
	{
		val list = new ArrayList<BiFunction<HttpServerExchange, Map<String, String>, Boolean>>();
		list.add( this::matchesVirtualHost );
		list.add( this::matchesPath );
		return list;
	}

	public Boolean matchesVirtualHost( final HttpServerExchange exchange, final Map<String, String> properties )
	{
		val hostHeader = exchange.getRequestHeaders().getFirst( Headers.HOST );
		if ( hostHeader == null )
			return true;
		val host = stripHostFromHeader( hostHeader );
		return virtualHost.matches( host, properties );
	}

	String stripHostFromHeader( final String hostHeader )
	{
		val cursor = new StringCursor( hostHeader );
		if ( !cursor.shiftCursorToNextChar( ':' ) )
			cursor.end();
		else
			cursor.prev();
		val host = cursor.substringUntilCursor();
		return host;
	}

	public Boolean matchesPath( final HttpServerExchange exchange, final Map<String, String> properties )
	{
		val relativePath = exchange.getRelativePath();
		return path.matches( relativePath, properties );
	}

	static class EmptyConsumer implements Consumer<HttpServerExchange> {
		@Override
		public void accept( final HttpServerExchange t ){}
	}
}