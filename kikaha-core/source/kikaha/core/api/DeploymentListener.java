package kikaha.core.api;

public interface DeploymentListener {

	void onDeploy( DeploymentContext context );

	void onUndeploy( DeploymentContext context );

}
