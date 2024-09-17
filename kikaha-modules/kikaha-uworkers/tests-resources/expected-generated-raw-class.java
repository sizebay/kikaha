package io.kikaha.sample;

import kikaha.uworkers.core.*;
import kikaha.uworkers.api.*;
import javax.inject.*;

@Singleton
@Worker( "no-exchange" )
@SuppressWarnings("unchecked")
public class GeneratedWorkerMethod4012354918 implements WorkerEndpointMessageListener {

	@Inject io.kikaha.sample.TargetClass listener;

	@Override
	public void onMessage( final Exchange exchange ) throws Throwable {
		try {
			listener.methodName(
				exchange.requestAs(java.lang.String.class)
			);
		} catch ( Throwable cause ) {
			exchange.reply( cause );
		} finally { exchange.acknowledge(); }
	}
}
