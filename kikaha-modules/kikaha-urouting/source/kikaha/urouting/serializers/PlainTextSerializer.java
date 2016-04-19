package kikaha.urouting.serializers;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import kikaha.urouting.api.AbstractSerializer;
import kikaha.urouting.api.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@ContentType(Mimes.PLAIN_TEXT)
@Singleton
@Typed( Serializer.class )
public class PlainTextSerializer extends AbstractSerializer {

	@Override
	public <T> void serialize(T object, HttpServerExchange exchange)
			throws IOException {
		if ( ByteBuffer.class.isInstance(object) )
			send( exchange, (ByteBuffer)object );
		else if ( String.class.isInstance(object) )
			send( exchange, (String)object );
		else
			super.serialize(object, exchange);
	}

	void send( HttpServerExchange exchange, ByteBuffer buffer ){
		exchange.getResponseSender().send( buffer );
		exchange.dispatch();
	}

	void send( HttpServerExchange exchange, String string ){
		exchange.getResponseSender().send( string );
		exchange.dispatch();
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
