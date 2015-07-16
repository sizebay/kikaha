package kikaha.hazelcast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import kikaha.hazelcast.config.HazelcastTestCase;
import lombok.SneakyThrows;

import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.hazelcast.core.IQueue;

public class HazelcastQueueProducerTest extends HazelcastTestCase {

	static final Integer MAX_PRODUCED_JOBS = 100;

	final ServiceProvider provider = new DefaultServiceProvider();
	final QueueOfBooleansConsumer consumer = new QueueOfBooleansConsumer();

	@Provided
	@Source( "producedJobs" )
	IQueue<AtomicBoolean> producedJobs;

	@Provided
	@Source( "consumedJobs" )
	IQueue<AtomicBoolean> consumedJobs;
	CountDownLatch counter = new CountDownLatch( MAX_PRODUCED_JOBS );

	@Test
	public void ensureThatHaveProducedAMapAndCouldUseItAsPersistence() throws InterruptedException {
		for ( int i = 0; i < MAX_PRODUCED_JOBS; i++ )
			producedJobs.put( new AtomicBoolean( false ) );
		counter.await();
		for ( int i = 0; i < MAX_PRODUCED_JOBS; i++ )
			assertThat( consumedJobs.take().get(), is( true ) );
	}

	@Override
	@SneakyThrows
	public void provideExtraDependencies( ServiceProvider provider ) {
		provider.provideOn( consumer );
		consumer.start();
	}

	class QueueOfBooleansConsumer extends Thread {

		@Provided
		@Source( "producedJobs" )
		IQueue<AtomicBoolean> producedJobs;

		@Provided
		@Source( "consumedJobs" )
		IQueue<AtomicBoolean> consumedJobs;

		@Override
		public void run() {
			for ( int i = 0; i < MAX_PRODUCED_JOBS; i++ )
				try {
					final AtomicBoolean atomicBoolean = producedJobs.take();
					atomicBoolean.set( true );
					consumedJobs.put( atomicBoolean );
					counter.countDown();
				} catch ( final InterruptedException e ) {
					e.printStackTrace();
				}
		}
	}
}
