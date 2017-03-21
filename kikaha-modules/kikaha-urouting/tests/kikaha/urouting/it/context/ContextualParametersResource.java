package kikaha.urouting.it.context;


import javax.inject.*;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;
import kikaha.urouting.api.*;

@Path( "it/parameters/contextual" )
@Singleton
public class ContextualParametersResource {

	@GET
	@Path( "exchange" )
	public String headerFromServerExchange( @Context HttpServerExchange exchange ){
		return exchange.getRequestHeaders().get( "id" ).getFirst();
	}

	@GET
	@Path( "account" )
	public String accountNameFromAccount( @Context Account account ){
		return account.getPrincipal().getName();
	}

	@GET
	@Path( "security-context" )
	public String accountNameFromSecurityContext( @Context SecurityContext securityContext ){
		return securityContext.getAuthenticatedAccount().getPrincipal().getName();
	}

	@GET
	@Path( "session-context" )
	public String accountNameFromSecurityContext( @Context Session session ){
		return session.getAuthenticatedAccount().getPrincipal().getName();
	}
}
