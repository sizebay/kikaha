package kikaha.mojo;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;

/**
 *
 */
@Mojo( name = "deploy-on-aws-lambda", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class KikahaLambdaDeployerMojo extends AbstractMojo {

	final AWSCredentialsProviderChain credentials = DefaultAWSCredentialsProviderChain.getInstance();
	final AWS aws = new AWS();

	@Parameter( defaultValue = "false", required = true )
	Boolean enabled;

	@Parameter( defaultValue = "true", required = true )
	Boolean createRestAPI;

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

		configureAWS();

		final String parsedProjectName = projectName.replaceAll( "[._]", "-" );
		final String lambdaFunction = setupLambdaFunction( parsedProjectName );

		if ( createRestAPI )
			createRestAPI( parsedProjectName, lambdaFunction );
	}

	private void configureAWS() {
		aws.lambda = AWSLambdaClient.builder().withCredentials( credentials ).withRegion( Regions.fromName( regionName ) ).build();
		aws.sts = AWSSecurityTokenServiceClient.builder().withCredentials( credentials ).withRegion( Regions.fromName( regionName ) ).build();
		aws.apiGateway = AmazonApiGatewayClient.builder().withCredentials( credentials ).withRegion( Regions.fromName( regionName ) ).build();
	}

	String setupLambdaFunction(String projectName) {
		try {
			return updateLambdaFunction( projectName );
		} catch ( ResourceNotFoundException cause ) {
			return createLambdaFunction( projectName );
		}
	}

	String updateLambdaFunction( final String projectName ){
		final GetFunctionResult result = aws.getFunction( projectName );
		final String functionArn = result.getConfiguration().getFunctionArn();
		getLog().info( "Updating AWS Lambda Function '"+projectName+"'..." );
		aws.updateFunction( functionArn, s3Bucket, s3Key + ".jar" );
		return functionArn;
	}

	String createLambdaFunction( final String projectName ){
		getLog().info( "Creating AWS Lambda Function '"+projectName+"'..." );
		final CreateFunctionResult result = aws.createFunction(
				projectName, s3Bucket, s3Key + ".jar", lambdaTimeout, lambdaMemory, lambdaRole );
		return result.getFunctionArn();
	}

	void createRestAPI(String parsedProjectName, String lambdaFunction){
		RestApi result = aws.getRestApi( parsedProjectName );
		if ( result != null && force ) {
			getLog().warn( "Removing REST API '" + parsedProjectName + "' API Gateway. Reason: force=true." );
			aws.deleteRestApi( result.getId() );
			result = null;
		}

		if ( result == null ) {
			final String accountId = aws.getMyAccountId();
			setupApiGateway(accountId, lambdaFunction, parsedProjectName);
		}
	}

	void setupApiGateway( String accountId, String functionArn, String projectName ) {
		getLog().info( "Creating REST API '"+ projectName +"'..." );
		final String restApiID = aws.createRestApi(projectName).getId();
		String resourceId = aws.getRootResourceId( restApiID );
		getLog().info( "Pointing the all requests to lambda function '"+ functionArn +"'" );
		resourceId = aws.createProxyResource(resourceId, restApiID).getId();
		aws.putMethod(restApiID, resourceId);
		aws.assignLambdaToResource(restApiID, resourceId, functionArn);
		aws.deployFunction(restApiID);
		final String sourceArn = "arn:aws:execute-api:"+regionName+":"+accountId+":"+restApiID+"/*/*/*";
		aws.addPermissionToInvokeLambdaFunctions(projectName, sourceArn);
	}
}

