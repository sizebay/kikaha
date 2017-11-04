package kikaha.urouting.serializers.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import kikaha.cloud.aws.lambda.AmazonLambdaSerializer;
import lombok.*;

import java.io.IOException;
import java.util.*;
import javax.inject.*;

@Singleton
public class JacksonAmazonLambdaSerializer implements AmazonLambdaSerializer {

    @Inject Jackson jackson;

    @Override
    public String toString(Object body) {
        try {
            return jackson.objectMapper().writeValueAsString( body );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T fromString(String body, Class<T> clazz) {
        try {
            return jackson.objectMapper().readValue( body, clazz );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
