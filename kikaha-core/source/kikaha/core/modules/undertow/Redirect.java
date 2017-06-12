package kikaha.core.modules.undertow;

import io.undertow.server.*;
import io.undertow.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor( staticName = "to" )
public class Redirect implements DefaultResponseListener {

	final String location;

	@Override
	public boolean handleDefaultResponse( HttpServerExchange exchange ) {
		exchange.setStatusCode( StatusCodes.SEE_OTHER );
		exchange.getResponseHeaders().put( Headers.LOCATION, location );
		exchange.endExchange();
		return true;
	}

	public static void to(HttpServerExchange exchange, final String location) {
		exchange.addDefaultResponseListener( Redirect.to(location) );
		exchange.endExchange();
	}
}