package io.skullabs.undertow.standalone.api;

public interface DeploymentHook {

	void onDeploy( DeploymentContext context );

	void onUndeploy( DeploymentContext context );

}
