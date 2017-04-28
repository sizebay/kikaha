package kikaha.mojo;

import java.util.List;
import java.util.function.Function;
import com.amazonaws.services.apigateway.model.*;
import com.amazonaws.services.lambda.model.*;
import com.amazonaws.services.lambda.model.Runtime;

interface AWS {

	static DeleteRestApiRequest deleteRestApi( String id ) {
		return new DeleteRestApiRequest().withRestApiId( id );
	}

	static PutIntegrationRequest putIntegration( String restApiID, String resourceId, String functionArn ) {
		return new PutIntegrationRequest().withRestApiId( restApiID ).withResourceId( resourceId )
				.withUri( "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/" + functionArn + "/invocations" )
				.withHttpMethod( "ANY" ).withType( IntegrationType.AWS )
				.withIntegrationHttpMethod( "POST" );
	}

	static GetResourcesRequest getResources( String restApiID ) {
		return new GetResourcesRequest().withRestApiId( restApiID ).withLimit( 1 );
	}

	static GetFunctionRequest getFunction( String name ) {
		return new GetFunctionRequest().withFunctionName( name );
	}

	static CreateFunctionRequest createFunction( String name, String s3Bucket, String s3Key, int timeout, int memory, String lambdaRole ) {
		final FunctionCode functionCode = new FunctionCode().withS3Bucket( s3Bucket ).withS3Key( s3Key );
		return new CreateFunctionRequest().withFunctionName( name ).withCode( functionCode ).withRole( lambdaRole )
				.withRuntime( Runtime.Java8 ).withHandler( "kikaha.cloud.aws.lambda.AmazonLambdaMethodData" )
				.withTimeout( timeout ).withMemorySize( memory );
	}

	static UpdateFunctionCodeRequest updateFunction( String name, String s3Bucket, String s3Key ) {
		return new UpdateFunctionCodeRequest().withFunctionName( name ).withS3Bucket( s3Bucket ).withS3Key( s3Key );
	}

	static CreateRestApiRequest createRestApi( String name ){
		return new CreateRestApiRequest().withName( name );
	}

	static PutMethodRequest putMethod( String restApiId, String resourceId ){
		return new PutMethodRequest().withHttpMethod( "ANY" ).withAuthorizationType( "NONE" )
				.withRestApiId( restApiId ).withResourceId( resourceId );
	}

	static <T> T first( List<T> list ) {
		return list.get(0);
	}

	static <T> T first( Iterable<T> list, Function<T, Boolean> condition ) {
		for ( T obj : list )
			if ( condition.apply( obj ) )
				return obj;
		return null;
	}

	static void await() {
		try {
			Thread.sleep( 1000 );
		} catch ( InterruptedException e ) {
			throw new IllegalStateException( e );
		}
	}
}