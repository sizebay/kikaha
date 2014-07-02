package io.skullabs.undertow.routing.samples;

import io.skullabs.undertow.urouting.api.Context;
import io.skullabs.undertow.urouting.api.CookieParam;
import io.skullabs.undertow.urouting.api.GET;
import io.skullabs.undertow.urouting.api.HeaderParam;
import io.skullabs.undertow.urouting.api.Path;
import io.skullabs.undertow.urouting.api.PathParam;
import io.skullabs.undertow.urouting.api.Produces;
import io.skullabs.undertow.urouting.api.QueryParam;
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
