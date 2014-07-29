package io.skullabs.undertow.standalone.api;

public interface RequestHookChain {

	void executeNext() throws UndertowStandaloneException;

	DeploymentContext context();

	boolean isInIOThread();

	void executeInIOThread( RequestHook hook ) throws UndertowStandaloneException;

}