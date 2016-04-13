package kikaha.core.modules.rewrite;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;

@RequiredArgsConstructor
public class RewriteRequestHttpHandler implements HttpHandler {

	final RequestMatcher requestMatcher;
	final URLMatcher targetPath;
	final HttpHandler next;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		val properties = new HashMap<String, String>();
		if ( requestMatcher.apply( exchange, properties ) )
			exchange.setRelativePath( targetPath.replace( properties ) );
		next.handleRequest(exchange);
	}

	public static HttpHandler from( final RewritableRule rule, final HttpHandler next )
	{
		return new RewriteRequestHttpHandler(
			DefaultMatcher.from( rule ),
			URLMatcher.compile( rule.target() ), next );
	}
}
