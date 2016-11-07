package kikaha.uworkers.local;

import static org.junit.Assert.*;
import java.util.concurrent.*;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link LocalEndpointFactory}.
 */
@RunWith(KikahaRunner.class)
public class LocalEndpointFactoryTest {

	@Inject LocalEndpointFactory factory;

	@Test
	public void ensureCanInstantiateASupplierWithFixedPoolAsConfiguredOnConfigFile(){
		final LocalEndpointInboxSupplier supplier = factory.createSupplier("configured");
		assertTrue( ArrayBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( 20, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateASupplierWithElasticPoolAsDefaultBehavior(){
		final LocalEndpointInboxSupplier supplier = factory.createSupplier("not-configured");
		assertTrue( LinkedBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( Integer.MAX_VALUE, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateAWorkerRefWithFixedPoolAsConfiguredOnConfigFile(){
		final LocalWorkerRef workerRef = factory.createWorkerRef("configured");
		assertTrue( ArrayBlockingQueue.class.isInstance( workerRef.supplier.messageQueue ) );
		assertEquals( 20, workerRef.supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateAWorkerRefWithElasticPoolAsDefaultBehavior(){
		final LocalWorkerRef workerRef = factory.createWorkerRef("not-configured");
		assertTrue( LinkedBlockingQueue.class.isInstance( workerRef.supplier.messageQueue ) );
		assertEquals( Integer.MAX_VALUE, workerRef.supplier.messageQueue.remainingCapacity() );
	}
}
