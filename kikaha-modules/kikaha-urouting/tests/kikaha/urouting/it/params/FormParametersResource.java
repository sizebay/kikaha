package kikaha.urouting.it.params;

import javax.inject.Singleton;
import kikaha.urouting.api.*;

/**
 * @author: miere.teixeira
 */
@Path( "it/parameters/form" )
@Singleton
public class FormParametersResource {

	@POST
	public long pathParameterEchoWithPOSTMethod( @FormParam( "id" ) long id ) {
		return id;
	}

	@Path( "multi" )
	@MultiPartFormData
	public long pathParameterEchoWithMULTIPartFormDataMethod( @FormParam( "id" ) long id ) {
		return id;
	}

	@PUT
	public long pathParameterEchoWithPUTMethod( @FormParam( "id" ) long id ) {
		return id;
	}

	@PATCH
	public long pathParameterEchoWithPATCHMethod( @FormParam( "id" ) long id ) {
		return id;
	}
}
