package io.skullabs.undertow.standalone.api;

public interface RequestHookChain {

	void executeNext() throws UndertowStandaloneException;
	DeploymentContext context();

}