package kikaha.core.impl;

import io.undertow.server.HttpServerExchange;

import java.util.Iterator;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.RequestHook;
import kikaha.core.api.RequestHookChain;
import kikaha.core.api.KikahaException;
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
	public void executeNext() throws KikahaException {
		final RequestHook hook = getNextHookClass();
		executeHook( hook );
	}

	@Override
	public boolean isInIOThread() {
		return this.exchange.isInIoThread();
	}

	@Override
	public void executeInWorkerThread( final Runnable hook ) throws KikahaException {
		if ( this.isInIOThread() )
			this.dispatchToWorkerThread( hook );
		else {
			System.out.println( "just run" );
			hook.run();
		}
	}

	public void dispatchToWorkerThread( Runnable hook ) {
		this.exchange.dispatch( hook );
	}

	void executeHook( final RequestHook hook ) throws KikahaException {
		hook.execute( this, this.exchange );
	}

	public RequestHook getNextHookClass() throws KikahaException {
		if ( !this.hooks().hasNext() )
			throw new KikahaException( "No hook available found." );
		return this.hooks().next();
	}
}
