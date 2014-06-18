package io.skullabs.undertow.routing;

import urouting.api.*;

@Path( "{contentType}/serasa" )
public class SampleRoute {

	@GET
	@Path( "relatomais" )
	public void renderRelatoMais(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id ) {

	}
}
