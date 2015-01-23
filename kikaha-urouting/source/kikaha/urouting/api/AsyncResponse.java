package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.urouting.ResponseWriter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import trip.spi.ServiceProviderException;

@Log
@AllArgsConstructor
@RequiredArgsConstructor
public class AsyncResponse {

	final HttpServerExchange exchange;
	final ResponseWriter writer;
	String contentType;

	public void write( final Response response ) {
		try {
			writeWithTheRightContentType( response );
		} catch ( ServiceProviderException | IOException e ) {
			log.severe( e.getMessage() );
			e.printStackTrace();
		}
	}

	void writeWithTheRightContentType( Response response )
		throws ServiceProviderException, RoutingException, IOException
	{
		if ( contentType != null )
			response = new WrappedResponse( response ).contentType( contentType );
		writer.write( exchange, response );
	}
}
