package kikaha.uworkers.it;

import java.util.concurrent.CountDownLatch;
import javax.inject.*;
import kikaha.uworkers.api.*;

/**
 *
 */
@Singleton
@SuppressWarnings("unused")
public class CounterWorker {

	static final int MANY_TIMES = 20;
	CountDownLatch counter;

	public void initialize(){
		counter = new CountDownLatch( MANY_TIMES );
	}

	@Worker( value = "count-down")
	public void count( Object object ){
		counter.countDown();
	}

	@Worker( value = "get-count")
	public void getCount(Exchange exchange) {
		exchange.reply( counter.getCount() );
	}
}
