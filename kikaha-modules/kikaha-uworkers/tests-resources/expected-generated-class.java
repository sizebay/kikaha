package io.kikaha.sample;

import kikaha.uworkers.core.*;
import kikaha.uworkers.api.*;
import javax.inject.*;

@Singleton
@Worker( "with-exchange" )
@SuppressWarnings("unchecked")
public class GeneratedWorkerMethod1212782385 implements WorkerEndpointMessageListener {

	@Inject io.kikaha.sample.TargetClass listener;

	@Override
	public void onMessage( final Exchange exchange ) throws Throwable {
		try {
			listener.methodName(
				exchange
			);
		} catch ( Throwable cause ) {
			exchange.reply( cause );
		} finally {  }
	}
}
