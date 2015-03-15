package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import kikaha.urouting.ResponseWriter;
import kikaha.urouting.RoutingMethodExceptionHandler;
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
	final RoutingMethodExceptionHandler exceptionHandler;
	String contentType;

	public void write( final Response response ) {
		try {
			writeWithTheRightContentType( response );
		} catch ( Throwable e ) {
			handleFailure( e );
		}
	}

	void writeWithTheRightContentType( Response response )
		throws ServiceProviderException, RoutingException, IOException
	{
		if ( contentType != null )
			response = new WrappedResponse( response ).contentType( contentType );
		writer.write( exchange, response );
	}

	void handleFailure( Throwable e ) {
		log.severe( e.getMessage() );
		e.printStackTrace();
		try {
			writer.write( exchange, exceptionHandler.handle( e ) );
		} catch ( Throwable cause ) {
			cause.printStackTrace();
			exchange.endExchange();
		}
	}
}
