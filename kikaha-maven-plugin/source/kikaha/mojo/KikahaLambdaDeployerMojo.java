package kikaha.mojo;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.*;
import com.amazonaws.services.apigateway.model.*;
import com.amazonaws.services.lambda.*;
import com.amazonaws.services.lambda.model.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;

/**
 *
 */
@Mojo( name = "deploy-on-aws-lambda", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class KikahaLambdaDeployerMojo extends AbstractMojo {

	final AWSCredentialsProviderChain credentials = DefaultAWSCredentialsProviderChain.getInstance();

	@Parameter( defaultValue = "false", required = true )
	Boolean enabled;

	@Parameter( defaultValue = "false", required = true )
	Boolean force;

	@Parameter( defaultValue = "us-east-1", required = true )
	String regionName;

	@Parameter( defaultValue = "60", required = true )
	Integer lambdaTimeout;

	@Parameter( defaultValue = "128", required = true )
	Integer lambdaMemory;

	@Parameter( defaultValue = "${project.groupId}-${project.artifactId}", required = true )
	String projectName;

	@Parameter( defaultValue = "${project.groupId}-${project.artifactId}", required = true )
	String s3Key;

	@Parameter( required = true )
	String s3Bucket;

	@Parameter( required = true )
	String lambdaRole;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ( !enabled ) return;

		getLog().info( "Setting up lambda function " + projectName );

		final String parsedProjectName = projectName.replaceAll( "[._]", "-" );
		final String lambdaFunction = setupLambdaFunction( parsedProjectName );

		getLog().warn( "Lambda Function: " + lambdaFunction );

		final AmazonApiGateway apiGateway = AmazonApiGatewayClient.builder().withCredentials( credentials ).withRegion( Regions.fromName( regionName ) ).build();
		RestApi result = AWS.first(apiGateway.getRestApis( new GetRestApisRequest() ).getItems(), api->api.getName().equals( parsedProjectName ) );

		if ( result != null && force ) {
			getLog().warn( "Cleaning up former REST API on API Gateway..." );
			apiGateway.deleteRestApi( AWS.deleteRestApi( result.getId() ) );
			result = null;
		}

		if ( result == null ) {
			getLog().info( "Configuring REST API on API Gateway..." );
			setupApiGateway( apiGateway, lambdaFunction, parsedProjectName );
		}
	}

	String setupLambdaFunction( String projectName ) {
		final AWSLambda lambda = AWSLambdaClient.builder().withCredentials( credentials ).withRegion( Regions.fromName( regionName ) ).build();
		try {
			return updateLambdaFunction( lambda, projectName );
		} catch ( ResourceNotFoundException cause ) {
			return createLambdaFunction( lambda, projectName );
		}
	}

	String updateLambdaFunction( final AWSLambda lambda, final String projectName ){
		final GetFunctionResult result = lambda.getFunction( AWS.getFunction( projectName ) );
		AWS.await();
		final String functionArn = result.getConfiguration().getFunctionArn();
		getLog().info( "Updating AWS Lambda Function..." );
		lambda.updateFunctionCode( AWS.updateFunction( functionArn, s3Bucket, s3Key + ".jar" ) );
		return functionArn;
	}

	String createLambdaFunction( final AWSLambda lambda, final String projectName ){
		final CreateFunctionResult result = lambda.createFunction( AWS.createFunction(
				projectName, s3Bucket, s3Key + ".jar", lambdaTimeout, lambdaMemory, lambdaRole ) );
		AWS.await();
		return result.getFunctionArn();
	}

	void setupApiGateway( final AmazonApiGateway apiGateway, String functionArn, String projectName ) {
		getLog().info( "Creating AWS Lambda Function..." );
		final String restApiID = apiGateway.createRestApi( AWS.createRestApi( projectName ) ).getId();
		AWS.await();
		String resourceId = AWS.first( apiGateway.getResources( AWS.getResources( restApiID ) ).getItems() ).getId();
		AWS.await();
		resourceId = apiGateway.createResource( new CreateResourceRequest().withParentId( resourceId ).withRestApiId( restApiID ).withPathPart( "{proxy+}" ) ).getId();
		AWS.await();
		apiGateway.putMethod( AWS.putMethod( restApiID, resourceId ) );
		AWS.await();
		apiGateway.putIntegration( AWS.putIntegration( restApiID, resourceId, functionArn ) );
	}
}

