package kikaha.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import kikaha.core.test.KikahaRunner;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(KikahaRunner.class)
public class HazelcastInstanceProducerTest {

	@Inject
	HazelcastInstanceProducer producer;

	@Inject
	HazelcastStubConfigurationListener stubListener;

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

	@Test
	public void ensureThatGetMapMethodWillProduceTheSameIMap(){
		final HazelcastInstance firstInstance = producer.createHazelcastInstance();
		final IMap<String, Object> firstMap = produceMap( firstInstance );
		final IMap<String, Object> secondMap = produceMap( firstInstance );
		assertSame( firstMap, secondMap );
	}

	@Test
	public void ensureThatHazelcastConfigurationListenersAreCalled(){
		assertTrue( stubListener.configurationLoaded );
	}

	IMap<String, Object> produceMap( HazelcastInstance instance ) {
		return instance.getMap( "map" );
	}

	@Getter
	static class Hello implements Serializable {

		private static final long serialVersionUID = 46565467897976546L;
		long now = new Date().getTime();

	}
}
