package io.skullabs.undertow.urouting.samples;

import io.skullabs.undertow.urouting.User;
import io.skullabs.undertow.urouting.api.Context;
import io.skullabs.undertow.urouting.api.DELETE;
import io.skullabs.undertow.urouting.api.POST;
import io.skullabs.undertow.urouting.api.PUT;
import io.skullabs.undertow.urouting.api.Path;
import io.skullabs.undertow.urouting.api.PathParam;
import io.skullabs.undertow.urouting.api.Response;
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
