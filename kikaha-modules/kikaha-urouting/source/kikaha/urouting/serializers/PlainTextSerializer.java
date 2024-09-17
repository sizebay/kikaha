package kikaha.urouting.serializers;

import java.io.*;
import java.nio.ByteBuffer;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;

@ContentType(Mimes.PLAIN_TEXT)
@Singleton
@Typed( Serializer.class )
public class PlainTextSerializer extends AbstractSerializer {

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange, String encoding)
			throws IOException {
		if ( ByteBuffer.class.isInstance(object) )
			send( exchange, (ByteBuffer)object );
		else if ( String.class.isInstance(object) )
			send( exchange, (String)object );
		else
			super.serialize(object, exchange, encoding);
	}

	void send( HttpServerExchange exchange, ByteBuffer buffer ){
		exchange.getResponseSender().send( buffer );
	}

	void send( HttpServerExchange exchange, String string ){
		exchange.getResponseSender().send( string );
	}

	@Override
	public <T> void serialize( final T object, final OutputStream output ) throws RoutingException {
		try {
			if ( object != null ) {
				final OutputStreamWriter writer = new OutputStreamWriter( output );
				writer.write( object.toString() );
				writer.close();
			}
		} catch ( final IOException cause ) {
			throw new RoutingException( cause );
		}
	}
}
