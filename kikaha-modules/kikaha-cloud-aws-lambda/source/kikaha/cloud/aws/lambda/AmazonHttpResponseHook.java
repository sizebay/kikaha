package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonHttpResponseHook {

	void apply( AmazonLambdaRequest request, AmazonLambdaResponse response );
}
