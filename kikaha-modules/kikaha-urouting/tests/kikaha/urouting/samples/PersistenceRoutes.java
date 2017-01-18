package kikaha.urouting.samples;

import java.util.concurrent.ExecutorService;

import kikaha.urouting.User;
import kikaha.urouting.api.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Path( "{contentType}/users" )
public class PersistenceRoutes {

	@Inject
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
		executor.submit( () -> response.write( DefaultResponse.notModified() ) );
	}

	@GET
	@Path( "async/{{id}}" )
	@Produces( Mimes.JSON )
	public void doAsyncSearchById(
		final AsyncResponse response, @PathParam( "id" ) final Long id ) {
		executor.submit( () -> response.write( DefaultResponse.notModified() ) );
	}

	@POST
	@Path("form")
	public String doSomethingWithFormData( @FormParam( "name" ) String name ){
		return name;
	}
}
