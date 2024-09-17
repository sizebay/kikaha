package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonLambdaContextProducer<T> {

	T produce( AmazonLambdaRequest request );
}
