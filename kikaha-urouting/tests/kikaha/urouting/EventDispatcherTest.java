package kikaha.urouting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import kikaha.urouting.EventDispatcher.Listener;
import kikaha.urouting.EventDispatcher.Matcher;
import lombok.RequiredArgsConstructor;

import org.junit.Before;
import org.junit.Test;

public class EventDispatcherTest {

	static final String DEFAULT_PASSWORD = "@dm1n";
	static final String DEFAULT_USERNAME = "admin";

	EventDispatcher<Credential> dispatcher;

	@Before
	public void configureEventDispatcher() {
		dispatcher = new EventDispatcher<>();
		dispatcher.when( new EmptyUsernameMatcher(), new UsernameDefaultValue( DEFAULT_USERNAME ) );
		dispatcher.when( new EmptyPasswordMatcher(), new PasswordDefaultValue( DEFAULT_PASSWORD ) );
	}

	@Test
	public void ensureThatHandleEventForEmptyUserName() {
		final Credential Credential = new Credential();
		Credential.password = DEFAULT_PASSWORD;
		assertTrue( dispatcher.apply( Credential ) );
		ensureUserHaveDefaultValues( Credential );
	}

	@Test
	public void ensureThatHandleEventForEmptyPassword() {
		final Credential Credential = new Credential();
		Credential.username = DEFAULT_USERNAME;
		assertTrue( dispatcher.apply( Credential ) );
		ensureUserHaveDefaultValues( Credential );
	}

	@Test
	public void ensureThatCouldNotHandleEventForAFullFilledCredentialInstance() {
		final Credential Credential = new Credential();
		Credential.password = DEFAULT_PASSWORD;
		Credential.username = DEFAULT_USERNAME;
		assertFalse( dispatcher.apply( Credential ) );
	}

	void ensureUserHaveDefaultValues( final Credential Credential ) {
		assertThat( Credential.username, is( DEFAULT_USERNAME ) );
		assertThat( Credential.password, is( DEFAULT_PASSWORD ) );
	}
}

class Credential {
	String username;
	String password;
}

class EmptyPasswordMatcher implements Matcher<Credential> {

	@Override
	public boolean matches( final Credential object ) {
		return object.password == null
			|| object.password.isEmpty();
	}
}

class EmptyUsernameMatcher implements Matcher<Credential> {

	@Override
	public boolean matches( final Credential object ) {
		return object.username == null
			|| object.username.isEmpty();
	}
}

@RequiredArgsConstructor
class UsernameDefaultValue implements Listener<Credential> {

	final String value;

	@Override
	public void onNewState( final Credential object ) {
		object.username = value;
	}
}

@RequiredArgsConstructor
class PasswordDefaultValue implements Listener<Credential> {

	final String value;

	@Override
	public void onNewState( final Credential object ) {
		object.password = value;
	}
}