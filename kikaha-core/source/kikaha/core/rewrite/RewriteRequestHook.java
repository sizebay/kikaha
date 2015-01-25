package kikaha.core.rewrite;

import io.undertow.server.HttpServerExchange;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.UndertowStandaloneException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RewriteRequestHook implements RequestHook {

	final Rewriter rewriteRule;

	@Override
	public void execute( final RequestHookChain chain, final HttpServerExchange exchange ) throws UndertowStandaloneException
	{
		rewriteRule.rewrite( exchange );
		chain.executeNext();
	}
}
