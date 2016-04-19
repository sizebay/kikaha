package kikaha.urouting.api;

import io.undertow.server.HttpServerExchange;
import kikaha.urouting.ResponseWriter;
import kikaha.urouting.RoutingMethodExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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
		throws RoutingException, IOException
	{
		if ( contentType != null )
			response = new WrappedResponse( response ).contentType( contentType );
		writer.write( exchange, response );
	}

	void handleFailure( Throwable e ) {
		log.error( e.getMessage(), e );
		try {
			writer.write( exchange, exceptionHandler.handle( e ) );
		} catch ( Throwable cause ) {
			log.error( cause.getMessage(), cause );
			exchange.endExchange();
		}
	}
}
