package kikaha.core.api;

public interface DeploymentHook {

	void onDeploy( DeploymentContext context );

	void onUndeploy( DeploymentContext context );

}
