package io.skullabs.undertow.routing.samples;

import io.skullabs.undertow.routing.User;
import io.skullabs.undertow.urouting.Mimes;
import io.skullabs.undertow.urouting.api.*;
import trip.spi.Service;

@Service
@Path( "sample" )
public class SampleRoute {

	@GET
	public String printUser() {
		return "User";
	}

	@GET
	@Path( "json" )
	@Produces( Mimes.JSON )
	public User printUserAsJSON() {
		return new User( "Handom Wser" );
	}
}
