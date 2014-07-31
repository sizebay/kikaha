package io.skullabs.undertow.hazelcast;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

public class HazelcastMapProducerTest {

	final ServiceProvider provider = new ServiceProvider();

	@Provided
	@Source( "users" )
	IMap<Long, User> users;

	@Test
	public void ensureThatHaveProducedAMapAndCouldUseItAsPersistence() {
		final User user = new User( "Jweroslavyou Smytch" );
		persist( user );
		final User persistedUser = retrieveById( user.getId() );
		assertEquals( user, persistedUser );
	}

	User retrieveById( Long id ) {
		return users.get( id );
	}

	void persist( User user ) {
		Long id = user.getId();
		users.put( id, user );
	}

	@Before
	public void setup() throws ServiceProviderException {
		provider.provideOn( this );
	}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
class User implements Serializable {

	private static final long serialVersionUID = -6264487228493293513L;

	final Long id = new Date().getTime();
	final String name;
}