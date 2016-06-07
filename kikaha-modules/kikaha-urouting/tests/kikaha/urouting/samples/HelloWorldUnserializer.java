package kikaha.urouting.samples;

import java.io.IOException;
import javax.inject.Singleton;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;
import lombok.Getter;

/**
 *
 */
@Singleton
@ContentType("text/hello")
public class HelloWorldUnserializer implements Unserializer {

	@Getter
	volatile boolean methodCalled;

	@Override
	public <T> T unserialize(HttpServerExchange input, Class<T> targetClass, String encoding) throws IOException {
		methodCalled = true;
		return (T)"Hello World";
	}
}
