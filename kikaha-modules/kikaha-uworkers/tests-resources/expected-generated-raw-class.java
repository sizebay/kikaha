package io.kikaha.sample;

import kikaha.uworkers.core.*;
import kikaha.uworkers.api.*;
import javax.inject.*;

@Singleton
@Worker( endpoint = "no-exchange", alias = "no-exchange" )
@SuppressWarnings("unchecked")
public class GeneratedRoutingMethod770339057 implements WorkerEndpointMessageListener {

	@Inject io.kikaha.sample.TargetClass listener;

	@Override
	public void onMessage( final Exchange exchange ) throws Throwable {
		try {
			listener.methodName(
				(java.lang.String)exchange.request()
			);
		} catch ( Throwable cause ) {
			exchange.reply( cause );
		}
	}
}
