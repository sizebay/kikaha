package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;

import java.util.HashMap;

import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import kikaha.core.api.conf.RewritableRule;
import kikaha.core.url.URLMatcher;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class RewriteRequestHook implements RequestHook {

	final RequestMatcher requestMatcher;
	final URLMatcher targetPath;

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange ) throws UndertowStandaloneException
	{
		val properties = new HashMap<String, String>();
		if ( requestMatcher.apply( exchange, properties ) )
			exchange.setRelativePath( targetPath.replace( properties ) );
		chain.executeNext();
	}

	public static RequestHook from( final RewritableRule rule )
	{
		return new RewriteRequestHook(
			DefaultMatcher.from( rule ),
			URLMatcher.compile( rule.target() ) );
	}
}
