package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonHttpHandler {

	AmazonLambdaResponse handle( AmazonLambdaRequest request ) throws Exception;
}
