package kikaha.urouting.it.params;

import javax.inject.Singleton;

import io.undertow.util.FileUtils;
import kikaha.urouting.api.*;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;

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

	@Path( "multi-with-file" )
	@MultiPartFormData
	@SneakyThrows
	public String pathParameterEchoWithMULTIPartFormDataMethodContainsFile( @FormParam( "file" ) File file ) {
		String response = FileUtils.readFile( new FileInputStream(file) );
		return response;
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
