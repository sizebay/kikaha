package kikaha.urouting.it.responses;

import javax.inject.Singleton;
import java.util.*;
import io.undertow.util.Headers;
import kikaha.urouting.api.*;

/**
 *
 */
@Path( "it/parameters/serialized" )
@Singleton
public class SerializedResponsesResource {

	@GET
	@Path( "no-content" )
	public void noContent(){}

	@GET
	@Path( "native-as-default-type" )
	public Map<String, Integer> serializingNativeObjectAsDefaultType(){
		return Collections.singletonMap( "id", 12 );
	}

	@GET
	@Path( "native-as-yaml" )
	@Produces( YmlSerializer.MIME )
	public Map<String, Integer> serializingNativeObjectAsYaml(){
		return Collections.singletonMap( "id", 12 );
	}

	@GET
	@Path( "response-as-default-type" )
	public Response serializingResponseObjectAsDefaultType(){
		return DefaultResponse.ok()
				.entity( Collections.singletonMap( "id", 12 ) );
	}

	@GET
	@Path( "response-as-yaml" )
	@Produces( YmlSerializer.MIME )
	public Map<String, Integer> serializingResponseObjectAsYaml(){
		return Collections.singletonMap( "id", 12 );
	}

	@GET
	@Path( "response-as-yaml2" )
	public Response serializingResponseObjectAsYaml2(){
		return DefaultResponse.ok()
				.entity( Collections.singletonMap( "id", 12 ) )
				.contentType( YmlSerializer.MIME );
	}

	@GET
	@Path( "response-as-yaml3" )
	public Response serializingResponseObjectAsYaml3(){
		return DefaultResponse.ok()
				.entity( Collections.singletonMap( "id", 12 ) )
				.header( Headers.CONTENT_TYPE, YmlSerializer.MIME );
	}

	@GET
	@Path("failure")
	public void raiseAnException(){
		throw new FailureException();
	}
}
