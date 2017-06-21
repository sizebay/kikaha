package kikaha.urouting.it.context;


import static kikaha.urouting.api.DefaultResponse.ok;
import javax.inject.*;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import kikaha.core.modules.security.*;
import kikaha.urouting.api.*;

@Singleton
public class ContextualParametersResource {

	@GET
	@Path( "it/parameters/contextual/exchange" )
	public String headerFromServerExchange( @Context HttpServerExchange exchange ){
		return exchange.getRequestHeaders().get( "id" ).getFirst();
	}

	@GET
	@Path( "it/parameters/contextual/exchange/async1" )
	public void headerFromServerExchange( @Context HttpServerExchange exchange, AsyncResponse asyncResponse ){
		final String id = exchange.getRequestHeaders().get("id").getFirst();
		asyncResponse.write( ok( id ).contentType( Mimes.PLAIN_TEXT ) );
	}

	@GET
	@Path( "it/parameters/contextual/account" )
	public String accountNameFromAccount( @Context Account account ){
		return account.getPrincipal().getName();
	}

	@GET
	@Path( "it/parameters/contextual/security-context" )
	public String accountNameFromSecurityContext( @Context SecurityContext securityContext ){
		return securityContext.getAuthenticatedAccount().getPrincipal().getName();
	}

	@GET
	@Path( "it/parameters/contextual/session-context" )
	public String accountNameFromSecurityContext( @Context Session session ){
		return session.getAuthenticatedAccount().getPrincipal().getName();
	}

	@GET
	@Path( "it/parameters/contextual/auth-not-required" )
	public String accountNameFromSecurityContextWhenNotAuthIsRequired( @Context Session session ){
		final Account account = session.getAuthenticatedAccount();
		return account == null ? "unknown" : account.getPrincipal().getName() ;
	}
}
