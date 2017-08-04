package kikaha.cloud.aws.lambda;

/**
 * Created by miere.teixeira on 03/08/2017.
 */
public interface AmazonAuthenticationMechanism<T> extends AmazonHttpInterceptor {

    @Override
    default void validateRequest(AmazonLambdaRequest request) throws AmazonLambdaFunctionInterrumptedException {
        final T credential = readCredentialFrom( request );

        AmazonLambdaResponse response;
        if ( credential == null || !authenticate( credential ) )
            response = sendAuthenticationChallenge( request );
        else
            response = sendAuthenticationSuccess( request );

        if ( response != null )
            throw new AmazonLambdaFunctionInterrumptedException( response );
    }

    T readCredentialFrom(AmazonLambdaRequest request);

    boolean authenticate(T credential);

    default AmazonLambdaResponse sendAuthenticationChallenge(AmazonLambdaRequest request) {
        return AmazonLambdaResponse.notAuthenticated();
    }

    default AmazonLambdaResponse sendAuthenticationSuccess(AmazonLambdaRequest request){
        return null;
    }

    @Override
    default void beforeSendResponse(AmazonLambdaResponse response) throws AmazonLambdaFunctionInterrumptedException{

    }
}
