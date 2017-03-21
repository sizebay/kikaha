package kikaha.urouting.it.params;

import javax.inject.Singleton;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
@Path( "it/parameters/cookie" )
@Singleton
public class CookieParametersResource {

	@GET
	public long pathParameterEchoWithGETMethod( @CookieParam( "id" ) long id ) {
		return id;
	}

	@POST
	public long pathParameterEchoWithPOSTMethod( @CookieParam( "id" ) long id ) {
		return id;
	}

	@PUT
	public long pathParameterEchoWithPUTMethod( @CookieParam( "id" ) long id ) {
		return id;
	}

	@DELETE
	public long pathParameterEchoWithDELETEMethod( @CookieParam( "id" ) long id ) {
		return id;
	}

	@PATCH
	public long pathParameterEchoWithPATCHMethod( @CookieParam( "id" ) long id ) {
		return id;
	}
}
