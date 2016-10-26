package kikaha.uworkers.core;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import javax.inject.Inject;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class WorkerRefProducerTest {

	@Inject WorkerRefProducer producer;

	@Inject
	@Worker( endpoint = "same-endpoint")
	WorkerRef injectedEndpoint;

	@Mock Worker workerAnnotation;
	@Mock ProviderContext providerContext;

	@Before
	public void configureTest(){
		MockitoAnnotations.initMocks(this);
		doReturn( workerAnnotation ).when( providerContext ).getAnnotation( eq( Worker.class ) );
	}

	@Test
	public void ensureCanInjectAWorkerRef() throws Exception {
		assertNotNull( injectedEndpoint );
	}

	@Test
	public void ensureCanProduceAWorkerRef(){
		doReturn( "any-endpoint" ).when( workerAnnotation ).endpoint();
		final WorkerRef workerRef = producer.produceAWorkerRef(providerContext);
		assertNotNull( workerRef );
		assertNotEquals( workerRef, injectedEndpoint );
	}

	@Test
	public void ensureCanProduceTheSameWorkerRefAlreadyInjectedOnThisTestClass(){
		doReturn( "same-endpoint" ).when( workerAnnotation ).endpoint();
		final WorkerRef workerRef = producer.produceAWorkerRef(providerContext);
		assertNotNull( workerRef );
		assertEquals( workerRef, injectedEndpoint );
	}
}