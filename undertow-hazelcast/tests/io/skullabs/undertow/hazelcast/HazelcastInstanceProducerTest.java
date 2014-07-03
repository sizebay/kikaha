package io.skullabs.undertow.hazelcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import trip.spi.*;

public class HazelcastInstanceProducerTest {

	final ServiceProvider provider = new ServiceProvider();

	@Provided
	HazelcastInstanceProducer producer;

	@Test
	public void grantThatProducesTwoMapsWithSameData() {
		final HazelcastInstance firstInstance = producer.createHazelcastInstance();
		final IMap<String, Object> firstMap = produceMap( firstInstance );
		final Hello hello = new Hello();
		firstMap.put( "now", hello );

		final HazelcastInstance secondInstance = producer.createHazelcastInstance();
		assertNotSame( firstInstance, secondInstance );
		final IMap<String, Object> secondMap = produceMap( secondInstance );
		assertNotSame( firstMap, secondMap );
		final Hello secondHello = (Hello)firstMap.get( "now" );
		assertEquals( secondHello.getNow(), hello.getNow() );
	}

	IMap<String, Object> produceMap( HazelcastInstance instance ) {
		return instance.getMap( "map" );
	}

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
	}

	@Getter
	static class Hello implements Serializable {

		private static final long serialVersionUID = 46565467897976546L;
		long now = new Date().getTime();

	}
}
