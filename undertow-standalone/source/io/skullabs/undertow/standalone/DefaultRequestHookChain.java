package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.DeploymentContext;
import io.skullabs.undertow.standalone.api.RequestHook;
import io.skullabs.undertow.standalone.api.RequestHookChain;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;
import io.undertow.server.HttpServerExchange;

import java.util.Iterator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class DefaultRequestHookChain implements RequestHookChain {

	final HttpServerExchange exchange;
	final DeploymentContext context;
	
	@Getter( lazy=true )
	private final Iterator<RequestHook> hooks = context.requestHooks().iterator();

	@Override
	public void executeNext() throws UndertowStandaloneException {
		RequestHook hook = getNextHookClass();
		hook.execute( this, this.exchange );
	}

	public RequestHook getNextHookClass() throws UndertowStandaloneException {
		if ( !this.hooks().hasNext() )
			throw new UndertowStandaloneException( "No hook available found." );
		return this.hooks().next();
	}
}
