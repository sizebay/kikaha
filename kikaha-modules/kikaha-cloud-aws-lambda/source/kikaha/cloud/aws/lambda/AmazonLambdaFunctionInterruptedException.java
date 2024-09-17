package kikaha.cloud.aws.lambda;

import lombok.*;

/**
 * Created by miere.teixeira on 03/08/2017.
 */
@RequiredArgsConstructor
public class AmazonLambdaFunctionInterruptedException extends RuntimeException {

    @NonNull
    final AmazonLambdaResponse response;
}
