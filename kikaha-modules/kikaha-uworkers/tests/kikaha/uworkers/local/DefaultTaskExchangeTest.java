package kikaha.uworkers.local;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static kikaha.uworkers.local.CounterInterceptor.andCount;
import static org.junit.Assert.assertEquals;
import java.util.concurrent.CountDownLatch;
import kikaha.uworkers.core.Threads;
import lombok.*;
import org.junit.Test;

/**
 * Unit test for {@link LocalExchange}.
 */
public class DefaultTaskExchangeTest {

	static final Ping ping = new Ping();
	static final Pong pong = new Pong( ping );

	@Test(timeout = 1000)
	@SneakyThrows
	public void receiveAsyncResponseWhenListenerIsSetBeforeResponseIsDefined() {
		final CountDownLatch counter = new CountDownLatch(1);
		final LocalExchange exchange = LocalExchange.of(ping);

		exchange.then( (e,t) -> {
			assertEquals( ping, e.as( Pong.class ).getPing() );
			counter.countDown();
		});

		newSingleThreadExecutor().submit( ()-> exchange.reply( pong ) );
		counter.await();
	}

	@Test(timeout = 15000)
	@SneakyThrows
	public void receiveAsyncResponseWhenListenerIsSetAfterResponseIsDefined() {
		final CountDownLatch counter = new CountDownLatch(1);
		final LocalExchange exchange = LocalExchange.of(ping);

		newSingleThreadExecutor().submit( ()-> exchange.reply( pong ) );

		exchange.then( (e,t) -> {
			assertEquals( ping, e.as( Pong.class ).getPing() );
			counter.countDown();
		});

		counter.await();
	}

	@Test(timeout = 1000)
	public void readSyncResponseWhenValueIsSetAlmostAtSameTimeItIsRequested() {
		final LocalExchange exchange = LocalExchange.of(ping);
		newSingleThreadExecutor().submit( ()-> exchange.reply( pong ) );
		final Pong pong = exchange.response();
		assertEquals( ping, pong.getPing() );
	}

	@Test(timeout = 3000)
	public void readSyncResponseWhenValueIsSetBeforeItIsRequested() {
		final LocalExchange exchange = LocalExchange.of(ping);
		newSingleThreadExecutor().submit( ()-> exchange.reply( pong ) );
		sleep(1);
		final Pong pong = exchange.response();
		assertEquals( ping, pong.getPing() );
	}

	@Test(timeout = 10000)
	public void readSyncResponseWhenValueIsSetAfterItIsRequested() {
		final LocalExchange exchange = LocalExchange.of(ping);
		newSingleThreadExecutor().submit( ()-> {
			sleep(1);
			exchange.reply( pong );
		} );
		final Pong pong = exchange.response();
		assertEquals( ping, pong.getPing() );
	}

	@Test( timeout = 30000 )
	public void doIntenseStressTest() throws InterruptedException {
		final CountDownLatch counter = new CountDownLatch(90);
		final Threads async = Threads.elasticPool();

		for ( int i=0; i<18; i++ ) {
			async.submit( andCount( counter, this::readSyncResponseWhenValueIsSetAlmostAtSameTimeItIsRequested ) );
			async.submit( andCount( counter, this::readSyncResponseWhenValueIsSetBeforeItIsRequested ) );
			async.submit( andCount( counter, this::readSyncResponseWhenValueIsSetAfterItIsRequested ) );
			async.submit( andCount( counter, this::receiveAsyncResponseWhenListenerIsSetAfterResponseIsDefined ) );
			async.submit( andCount( counter, this::receiveAsyncResponseWhenListenerIsSetBeforeResponseIsDefined ) );
		}

		counter.await();
		async.shutdown();
	}

	static void sleep( int secs ){
		try {
			Thread.sleep(secs * 1000l);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}

class CounterInterceptor implements Runnable {

	CountDownLatch counter;
	Runnable runnable;

	@Override
	public void run() {
		try { runnable.run(); } catch( Exception e ) { e.printStackTrace(); }
		counter.countDown();
	}

	public static CounterInterceptor andCount( CountDownLatch counter, Runnable runnable ) {
		final CounterInterceptor interceptor = new CounterInterceptor();
		interceptor.counter = counter;
		interceptor.runnable = runnable;
		return interceptor;
	}

	@Override
	public String toString() {
		return runnable.toString();
	}
}

final class Ping {}

@Value
@Getter
final class Pong {
	final Ping ping;
}