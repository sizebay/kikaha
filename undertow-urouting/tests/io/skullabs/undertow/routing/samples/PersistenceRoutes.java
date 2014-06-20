package io.skullabs.undertow.routing.samples;

import io.skullabs.undertow.routing.User;
import trip.spi.Service;
import urouting.api.*;

@Service
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
