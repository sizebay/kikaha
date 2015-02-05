package kikaha.core.api;

import io.undertow.server.HttpServerExchange;

public interface RequestHook {

	void execute(RequestHookChain chain, HttpServerExchange exchange) throws KikahaException;

}
