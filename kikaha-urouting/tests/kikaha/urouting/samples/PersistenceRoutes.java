package kikaha.urouting.samples;

import kikaha.urouting.User;
import kikaha.urouting.api.Context;
import kikaha.urouting.api.DELETE;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.PUT;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Response;
import trip.spi.Singleton;

@Singleton
@Path( "{contentType}/users" )
public class PersistenceRoutes {

	@PUT
	@Path( "{id}" )
	public Response update(
			@PathParam( "id" ) Long id, User user ) {
		return null;
	}

	@POST
	public Response create( User user ) {
		return null;
	}

	@DELETE
	@Path( "{id}" )
	public void delete( @Context User user ) {
	}
}
