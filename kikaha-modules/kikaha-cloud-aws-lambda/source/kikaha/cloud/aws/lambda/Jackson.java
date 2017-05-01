package kikaha.cloud.aws.lambda;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public interface Jackson {

	ObjectMapper JACKSON = new ObjectMapper();

	static String toJsonString(Object body) {
		try {
			return JACKSON.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	static <T> T fromJsonString(String body, Class<T> clazz) {
		try {
			return JACKSON.readValue( body, clazz );
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
