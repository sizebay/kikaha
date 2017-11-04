package kikaha.cloud.aws.lambda;

import kikaha.core.test.KikahaRunner;
import kikaha.core.util.SystemResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(KikahaRunner.class)
public class AmazonLambdaRequestSerializationTest {

    @Inject AmazonLambdaSerializer serializer;

    @Test
    public void canDeserializeRequestFromJSON(){
        final String requestAsString = SystemResource.readFileAsString( "aws-lambda-request.json", "UTF-8" );
        final AmazonLambdaRequest request = serializer.fromString( requestAsString, AmazonLambdaRequest.class );
        assertNotNull( request );
    }
}
