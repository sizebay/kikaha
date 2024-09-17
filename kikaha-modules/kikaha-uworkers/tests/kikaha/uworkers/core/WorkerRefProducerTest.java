package kikaha.uworkers.core;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import javax.inject.Inject;
import kikaha.core.cdi.ProviderContext;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.*;
import kikaha.uworkers.local.LocalWorkerRef;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class WorkerRefProducerTest {

	@Inject WorkerRefProducer producer;

	@Worker( value = "same-value")
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
		doReturn( "any-value" ).when( workerAnnotation ).value();
		final WorkerRef workerRef = producer.produceAWorkerRef(providerContext);
		assertNotNull( workerRef );
		assertNotEquals( workerRef, injectedEndpoint );
	}

	@Test
	public void ensureCanProduceTheSameWorkerRefAlreadyInjectedOnThisTestClass(){
		doReturn( "same-value" ).when( workerAnnotation ).value();
		final WorkerRef workerRef = producer.produceAWorkerRef(providerContext);
		assertNotNull( workerRef );
		assertEquals( workerRef, injectedEndpoint );

		final LocalWorkerRef localInjectedEndpoint = (LocalWorkerRef)injectedEndpoint;
		final LocalWorkerRef localWorkerRef = (LocalWorkerRef)workerRef;
		assertSame( localInjectedEndpoint.getSupplier(), localWorkerRef.getSupplier() );
	}
}