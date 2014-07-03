package io.skullabs.undertow.hazelcast;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import com.hazelcast.core.IQueue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import trip.spi.*;

public class HazelcastQueueProducerTest {

	static final Integer MAX_PRODUCED_JOBS = 100;

	final ServiceProvider provider = new ServiceProvider();
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

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
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
					AtomicBoolean atomicBoolean = producedJobs.take();
					atomicBoolean.set( true );
					consumedJobs.put( atomicBoolean );
					counter.countDown();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
		}
	}
}
