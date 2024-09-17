package kikaha.uworkers.local;

import static org.junit.Assert.*;
import java.util.concurrent.*;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.core.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for {@link LocalEndpointFactory}.
 */
@RunWith(KikahaRunner.class)
public class LocalEndpointFactoryTest {

	@Inject MicroWorkersContext context;
	@Inject LocalEndpointFactory factory;

	@Test
	public void ensureCanInstantiateASupplierWithFixedPoolAsConfiguredOnConfigFile(){
		final EndpointConfig endpointConfig = context.getEndpointConfig( "configured" );
		final LocalEndpointInboxSupplier supplier = factory.createSupplier( endpointConfig );
		assertTrue( ArrayBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( 20, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateASupplierWithElasticPoolAsDefaultBehavior(){
		final EndpointConfig endpointConfig = context.getEndpointConfig( "not-configured" );
		final LocalEndpointInboxSupplier supplier = factory.createSupplier( endpointConfig );
		assertTrue( LinkedBlockingQueue.class.isInstance( supplier.messageQueue ) );
		assertEquals( Integer.MAX_VALUE, supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateAWorkerRefWithFixedPoolAsConfiguredOnConfigFile(){
		final EndpointConfig endpointConfig = context.getEndpointConfig( "configured" );
		final LocalWorkerRef workerRef = factory.createWorkerRef( endpointConfig );
		assertTrue( ArrayBlockingQueue.class.isInstance( workerRef.supplier.messageQueue ) );
		assertEquals( 20, workerRef.supplier.messageQueue.remainingCapacity() );
	}

	@Test
	public void ensureCanInstantiateAWorkerRefWithElasticPoolAsDefaultBehavior(){
		final EndpointConfig endpointConfig = context.getEndpointConfig( "not-configured" );
		final LocalWorkerRef workerRef = factory.createWorkerRef( endpointConfig );
		assertTrue( LinkedBlockingQueue.class.isInstance( workerRef.supplier.messageQueue ) );
		assertEquals( Integer.MAX_VALUE, workerRef.supplier.messageQueue.remainingCapacity() );
	}
}
