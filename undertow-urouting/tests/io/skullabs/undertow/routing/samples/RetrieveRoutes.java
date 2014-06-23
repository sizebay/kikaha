package io.skullabs.undertow.routing.samples;

import io.skullabs.undertow.urouting.api.*;
import io.undertow.server.HttpServerExchange;

@Path( "{contentType}/serasa" )
public class RetrieveRoutes {

	@GET
	@Path( "relatomais" )
	public void renderRelatoMais(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id,
			@Context HttpServerExchange exchange ) {
		System.out.println( exchange.getRequestURI() );
		System.out.println( "contentType: " + contentType );
	}

	@GET
	@Path( "relatomais-xml" )
	@Produces( "application/json" )
	public String renderRelatoMaisXML(
			@HeaderParam( "Authorization-Token" ) Long authorizationToken,
			@CookieParam( "SESSIONID" ) Integer sessionId,
			@PathParam( "contentType" ) String contentType,
			@QueryParam( "id" ) Double id ) {
		System.out.println( "contentType: " + contentType );
		return "RelatoMais";
	}
}
