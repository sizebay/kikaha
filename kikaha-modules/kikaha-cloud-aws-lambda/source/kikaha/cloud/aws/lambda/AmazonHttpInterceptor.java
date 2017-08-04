package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonHttpInterceptor {

	void beforeSendResponse(AmazonLambdaResponse response) throws AmazonLambdaFunctionInterrumptedException;

	void validateRequest(AmazonLambdaRequest request) throws AmazonLambdaFunctionInterrumptedException;
}
