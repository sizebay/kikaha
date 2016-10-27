package kikaha.uworkers.it;

import static kikaha.uworkers.it.CounterWorker.MANY_TIMES;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.*;
import kikaha.uworkers.core.MicroWorkersTaskDeploymentModule;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class CounterWorkerTest {

	@Worker( endpoint = "count-down")
	WorkerRef countDownWorker;

	@Worker( endpoint = "get-count")
	WorkerRef getCountDownWorker;

	@Inject CounterWorker workerInstance;

	@Inject MicroWorkersTaskDeploymentModule module;

	@Before
	public void simulateWorkersDeployment() throws IOException {
		workerInstance.initialize();
		module.load( null, null );
	}

	@After
	public void shutdownWorkers(){
		module.unload();
	}

	@Test(timeout = 1000)
	public void ensureWorkerInstanceStartsTheCountingNumberAtItsInitialValue(){
		assertEquals( MANY_TIMES, workerInstance.counter.getCount() );
	}

	@Test(timeout = 1000)
	public void ensureWorkerInstanceCanCountDownByOne() throws InterruptedException {
		final Response response = countDownWorker.send();
		response.response();
		assertEquals( MANY_TIMES - 1, workerInstance.counter.getCount() );
	}

	@Test(timeout = 1000)
	public void ensureWorkerInstanceCanCountDownManyTimes() throws InterruptedException {
		for ( int i=0; i<MANY_TIMES; i++ )
			countDownWorker.send();
		workerInstance.counter.await();
	}

	@Test(timeout = 1000)
	public void ensureWorkerIsAbleToCountDownAndRetrieveZeroAsResponse() throws InterruptedException {
		for ( int i=0; i<MANY_TIMES; i++ )
			countDownWorker.send();
		workerInstance.counter.await();

		final long count = getCountDownWorker.send().response();
		assertEquals( 0, count );
	}
}