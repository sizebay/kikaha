package io.kikaha.sample;

import kikaha.uworkers.core.*;
import kikaha.uworkers.api.*;
import javax.inject.*;

@Singleton
@Worker( endpoint = "with-exchange", alias = "with-exchange" )
@SuppressWarnings("unchecked")
public class GeneratedRoutingMethod299710559 implements WorkerEndpointMessageListener {

	@Inject io.kikaha.sample.TargetClass listener;

	@Override
	public void onMessage( final Exchange exchange ) throws Throwable {
		try {
			listener.methodName(
				(java.lang.String)exchange
			);
		} catch ( Throwable cause ) {
			exchange.reply( cause );
		}
	}
}
