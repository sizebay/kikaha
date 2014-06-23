package io.skullabs.undertow.routing.samples;

import io.skullabs.undertow.urouting.api.*;

@Path( "{contentType}/serasa" )
public class RetrieveRoutes {

	@GET
	@Path( "relatomais" )
	public void renderRelatoMais(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id ) {

	}

	@GET
	@Path( "relatomais-xml" )
	@Produces( "application/json" )
	public String renderRelatoMaisXML(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id ) {
		return "RelatoMais";
	}
}
