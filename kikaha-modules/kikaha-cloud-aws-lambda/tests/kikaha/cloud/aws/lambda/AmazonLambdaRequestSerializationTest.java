package kikaha.cloud.aws.lambda;

import kikaha.core.util.SystemResource;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AmazonLambdaRequestSerializationTest {

    @Test
    public void canDeserializeRequestFromJSON(){
        final String requestAsString = SystemResource.readFileAsString( "aws-lambda-request.json", "UTF-8" );
        final AmazonLambdaRequest request = Jackson.fromJsonString( requestAsString, AmazonLambdaRequest.class );
        assertNotNull( request );
    }
}
