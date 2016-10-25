package io.kikaha.sample;

import kikaha.uworkers.core.*;
import kikaha.uworkers.api.*;
import javax.inject.*;

@Singleton
@SuppressWarnings("unchecked")
public class GeneratedRoutingMethod1479529980 implements WorkerEndpointMessageListener {

	@Inject io.kikaha.sample.TargetClass listener;

	@Override
	public void onMessage( final Exchange exchange ) throws Throwable {
		try {
			listener.methodName(
				exchange.request()
			);
		} catch ( Throwable cause ) {
			exchange.reply( cause );
		}
	}
}
