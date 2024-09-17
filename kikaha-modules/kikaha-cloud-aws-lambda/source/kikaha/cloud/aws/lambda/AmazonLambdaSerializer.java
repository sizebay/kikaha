package kikaha.cloud.aws.lambda;

/**
 *
 */
public interface AmazonLambdaSerializer {

	String toString(Object body);

	<T> T fromString(String body, Class<T> clazz);
}
