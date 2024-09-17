package kikaha.urouting.unit.samples;

import java.util.concurrent.*;

import kikaha.urouting.unit.User;
import kikaha.urouting.api.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Path( "{contentType}/users" )
public class PersistenceRoutes {

	ExecutorService executor = Executors.newSingleThreadExecutor();

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
