package kikaha.uworkers.core;

import kikaha.uworkers.api.*;

/**
 *
 */
public class SampleWorker {

	@Worker( value = "sample-1" )
	public void sample(Exchange exchange){
		final String ping = exchange.request();
		throw new UnsupportedOperationException("Not implemented yet: " + ping);
	}

	@Worker( value = "sample-2" )
	public void sample(String ping){
		throw new UnsupportedOperationException("Not implemented yet: " + ping);
	}
}
