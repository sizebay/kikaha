package kikaha.core.api;

import io.undertow.server.HttpServerExchange;

public interface RequestHookChain {

	void executeNext() throws UndertowStandaloneException;

	DeploymentContext context();

	HttpServerExchange exchange();

	boolean isInIOThread();

	void executeInIOThread( Runnable hook ) throws UndertowStandaloneException;

}