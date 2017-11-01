package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonHttpInterceptor {

	void beforeSendResponse(AmazonLambdaResponse response) throws AmazonLambdaFunctionInterruptedException;

	void validateRequest(AmazonLambdaRequest request) throws AmazonLambdaFunctionInterruptedException;
}
