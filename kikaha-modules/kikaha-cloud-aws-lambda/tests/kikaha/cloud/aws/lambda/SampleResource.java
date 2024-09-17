package kikaha.cloud.aws.lambda;

import io.undertow.security.idm.Account;
import kikaha.urouting.api.*;

/**
 * The only propose this resource class exists it to force the APT
 * to generate {@code routing classes} for each method. If the APT
 * would not able to generate and compile classes, it will break the
 * build plan of this module.
 */
@Path("root-uri")
@SuppressWarnings("unused")
public class SampleResource {

	@GET
	public Object doGetAndReturnObject() {
		return new Object();
	}

	@GET
	public Object doGetAndReturnObject(
		@CookieParam("JSESSIONID") String sessionId,
	    @HeaderParam("Cookie") String cookie,
	    @QueryParam("id") int id
	) {
		return new Object();
	}

	@GET
	@Path("{id}")
	public Long doGetAndReturnObject( @PathParam("id") Long id ) {
		return id;
	}

	@POST
	public Response doPost( Object body ) {
		return DefaultResponse.noContent();
	}

	@PUT
	public void doPutAndReceiveContext(@Context Account account, Object body){}
}
