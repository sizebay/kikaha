package kikaha.urouting.samples;

import java.util.concurrent.ExecutorService;

import kikaha.urouting.User;
import kikaha.urouting.api.AsyncResponse;
import kikaha.urouting.api.Context;
import kikaha.urouting.api.DELETE;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.PUT;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Produces;
import kikaha.urouting.api.Response;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
@Path( "{contentType}/users" )
public class PersistenceRoutes {

	@Provided
	ExecutorService executor;

	@PUT
	@Path( "{id}" )
	public Response update(
			@PathParam( "id" ) final Long id, final User user ) {
		return null;
	}

	@POST
	public Response create( final User user ) {
		return null;
	}

	@DELETE
	@Path( "{id}" )
	public void delete( @Context final User user ) {
	}

	@GET
	@Path( "async" )
	public void doAsyncSearch( final AsyncResponse response ) {
		executor.submit( ( ) -> {
			response.write( DefaultResponse.notModified() );
		} );
	}

	@GET
	@Path( "async/{{id}}" )
	@Produces( Mimes.JSON )
	public void doAsyncSearchById(
		final AsyncResponse response, @PathParam( "id" ) final Long id ) {
		executor.submit( ( ) -> {
			response.write( DefaultResponse.notModified() );
		} );
	}
}
