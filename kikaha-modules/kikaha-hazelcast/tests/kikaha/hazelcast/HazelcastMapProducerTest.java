package kikaha.hazelcast;

import com.hazelcast.core.IMap;
import kikaha.core.test.KikahaRunner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(KikahaRunner.class)
public class HazelcastMapProducerTest {

	@Inject
	@Named( "users" )
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
		final Long id = user.getId();
		users.put( id, user );
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