package kikaha.cdi.tests;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class SingletonProvidedProducerOfIntegers {

	final AtomicInteger counter = new AtomicInteger();

	@Produces
	public Integer produceInteger() {
		return counter.incrementAndGet();
	}
}
