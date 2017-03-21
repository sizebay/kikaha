package kikaha.urouting.it.responses;

import javax.inject.Singleton;
import java.io.IOException;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;
import org.yaml.snakeyaml.Yaml;

/**
 *
 */
@Singleton
@ContentType( YmlSerializer.MIME )
public class YmlSerializer implements Serializer {

	public static final String MIME = "application/yaml";

	final Yaml yaml = new Yaml();

	@Override
	public <T> void serialize( T object, HttpServerExchange output, String encoding ) throws IOException {
		final String dumped = yaml.dump( object );
		output.setStatusCode( 200 );
		output.getResponseSender().send( dumped );
		output.endExchange();
	}
}
