package kikaha.mustache;

import kikaha.urouting.api.GET;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Response;
import javax.inject.Singleton;

@Singleton
public class MustacheResource {

	final Object params = new Object();

	@GET
	@Path( "{templatePath}.do" )
	public Response renderTemplate(
		@PathParam( "templatePath" ) final String templatePath )
	{
		return MustacheResponse.ok()
			.templateName( templatePath + ".mustache" )
			.paramObject( params );
	}
}