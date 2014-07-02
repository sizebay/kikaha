package io.skullabs.undertow.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.skullabs.undertow.routing.samples.StringProducer;
import io.skullabs.undertow.urouting.api.ContextProducer;
import io.skullabs.undertow.urouting.api.ContextProducerFactory;
import io.skullabs.undertow.urouting.api.RoutingException;

import org.junit.Test;

import trip.spi.Provided;

public class ContextProducerFactoryTest extends TestCase {

	@Provided
	ContextProducerFactory producerFactory;

	@Test
	public void grantThatProducesAHelloWorld() throws RoutingException {
		final ContextProducer<String> stringProducer = producerFactory.producerFor( String.class );
		assertThat( stringProducer.produce( null ), is( StringProducer.HELLO_WORLD ) );
	}
}
