package kikaha.urouting.it.params;

import javax.inject.Singleton;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
@Path( "it/parameters/path/{id}" )
@Singleton
public class PathParametersResource {

	@GET
	public long pathParameterEchoWithGETMethod( @PathParam( "id" ) long id ) {
		return id;
	}

	@POST
	public long pathParameterEchoWithPOSTMethod( @PathParam( "id" ) long id ) {
		return id;
	}

	@PUT
	public long pathParameterEchoWithPUTMethod( @PathParam( "id" ) long id ) {
		return id;
	}

	@DELETE
	public long pathParameterEchoWithDELETEMethod( @PathParam( "id" ) long id ) {
		return id;
	}

	@PATCH
	public long pathParameterEchoWithPATCHMethod( @PathParam( "id" ) long id ) {
		return id;
	}
}
