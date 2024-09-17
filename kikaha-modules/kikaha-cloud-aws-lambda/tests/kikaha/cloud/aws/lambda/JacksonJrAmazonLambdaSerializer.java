package kikaha.cloud.aws.lambda;

import com.fasterxml.jackson.jr.ob.JSON;
import lombok.*;

import java.io.IOException;
import java.util.*;
import javax.inject.*;

@Singleton
@SuppressWarnings("unchecked")
public class JacksonJrAmazonLambdaSerializer implements AmazonLambdaSerializer {

    @Override
    public String toString(Object body) {
        try {
            return JSON.std.asString( body );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T fromString(String body, Class<T> clazz) {
        try {
            if ( Map.class.isAssignableFrom( clazz ) )
                return (T) JSON.std.anyFrom( body );
            return JSON.std.beanFrom( clazz, body );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
