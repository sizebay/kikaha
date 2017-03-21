package kikaha.urouting.it.params;

import javax.inject.Singleton;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
@Path( "it/parameters/query" )
@Singleton
public class QueryParametersResource {

	@GET
	public long pathParameterEchoWithGETMethod( @QueryParam( "id" ) long id ) {
		return id;
	}

	@POST
	public long pathParameterEchoWithPOSTMethod( @QueryParam( "id" ) long id ) {
		return id;
	}

	@PUT
	public long pathParameterEchoWithPUTMethod( @QueryParam( "id" ) long id ) {
		return id;
	}

	@DELETE
	public long pathParameterEchoWithDELETEMethod( @QueryParam( "id" ) long id ) {
		return id;
	}

	@PATCH
	public long pathParameterEchoWithPATCHMethod( @QueryParam( "id" ) long id ) {
		return id;
	}
}
