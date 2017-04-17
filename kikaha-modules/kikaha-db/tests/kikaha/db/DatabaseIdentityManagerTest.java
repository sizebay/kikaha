package kikaha.db;

import static org.junit.Assert.*;

import javax.inject.Inject;
import io.undertow.security.idm.*;
import kikaha.core.modules.security.UsernameAndPasswordCredential;
import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests for DatabaseIdentityManager.
 */
@RunWith( KikahaRunner.class )
public class DatabaseIdentityManagerTest {

	@Inject DatabaseIdentityManager identityManager;

	@Test
	public void ensureCanRetrieveUserAccountForValidCredentials(){
		final Credential credential = new UsernameAndPasswordCredential( "user", "pass" );
		final Account account = identityManager.verify( credential );
		assertNotNull( account );
		assertEquals( "user", account.getPrincipal().getName() );
		assertEquals( 1, account.getRoles().size() );
		assertTrue( account.getRoles().contains( "admin" ) );
	}
}