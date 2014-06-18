package io.skullabs.undertow.routing;

import trip.spi.Provided;
import urouting.api.*;

@Path( "{contentType}/serasa" )
public class SampleRoute {

	@Provided
	@GET
	@Path( "relatomais" )
	public void renderRelatoMais(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id ) {

	}
}
