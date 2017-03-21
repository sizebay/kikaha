package kikaha.urouting.it.params;

import javax.inject.Singleton;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
@Path( "it/parameters/header" )
@Singleton
public class HeaderParametersResource {

	@GET
	public long pathParameterEchoWithGETMethod( @HeaderParam( "id" ) long id ) {
		return id;
	}

	@POST
	public long pathParameterEchoWithPOSTMethod( @HeaderParam( "id" ) long id ) {
		return id;
	}

	@PUT
	public long pathParameterEchoWithPUTMethod( @HeaderParam( "id" ) long id ) {
		return id;
	}

	@DELETE
	public long pathParameterEchoWithDELETEMethod( @HeaderParam( "id" ) long id ) {
		return id;
	}

	@PATCH
	public long pathParameterEchoWithPATCHMethod( @HeaderParam( "id" ) long id ) {
		return id;
	}
}
