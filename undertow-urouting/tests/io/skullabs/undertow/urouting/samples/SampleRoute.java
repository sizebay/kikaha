package io.skullabs.undertow.urouting.samples;

import io.skullabs.undertow.urouting.User;
import io.skullabs.undertow.urouting.api.CPU;
import io.skullabs.undertow.urouting.api.GET;
import io.skullabs.undertow.urouting.api.Mimes;
import io.skullabs.undertow.urouting.api.Path;
import io.skullabs.undertow.urouting.api.Produces;
import trip.spi.Service;

@Service
@Path( "sample" )
public class SampleRoute {

	@GET
	@CPU
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
