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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(KikahaRunner.class)
public class HazelcastInstanceProducerTest {

	@Inject
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

	@Getter
	static class Hello implements Serializable {

		private static final long serialVersionUID = 46565467897976546L;
		long now = new Date().getTime();

	}
}
