package kikaha.core.api;

import io.undertow.server.HttpServerExchange;

public interface RequestHookChain {

	void executeNext() throws KikahaException;

	DeploymentContext context();

	HttpServerExchange exchange();

	boolean isInIOThread();

	void executeInWorkerThread( Runnable hook ) throws KikahaException;

}