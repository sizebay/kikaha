package com.texoit.undertow.standalone;

import io.undertow.server.HttpServerExchange;

import java.util.Iterator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.texoit.undertow.standalone.api.DeploymentContext;
import com.texoit.undertow.standalone.api.UndertowStandaloneException;
import com.texoit.undertow.standalone.api.RequestHook;
import com.texoit.undertow.standalone.api.RequestHookChain;

@Getter
@Accessors( fluent=true )
@RequiredArgsConstructor
public class DefaultRequestHookChain implements RequestHookChain {

	final Iterator<RequestHook> hooks;
	final HttpServerExchange exchange;
	final DeploymentContext context;

	@Override
	public void executeNext() throws UndertowStandaloneException {
		RequestHook hook = getNextHookClass();
		hook.execute( this, this.exchange );
	}

	public RequestHook getNextHookClass() throws UndertowStandaloneException {
		if ( !this.hooks.hasNext() )
			throw new UndertowStandaloneException( "No hook available found." );
		return this.hooks.next();
	}
}
