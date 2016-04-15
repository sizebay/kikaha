package kikaha.core.modules.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.X509CertificateCredential;
import kikaha.core.test.KikahaRunner;

import org.junit.Test;

import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(KikahaRunner.class)
public class DefaultAdminOnlyIdentityManagerTest {

	@Inject
	FixedUserAndPasswordIdentityManager identityManager;

	@Test
	public void ensureThatCouldValidateTestUserAsDefinedInApplicationConf() {
		String user = "test";
		UsernameAndPasswordCredential credential = new UsernameAndPasswordCredential( user, "t357" );
		Account account = identityManager.verify( credential );
		assertNotNull( account );
		assertEquals( user, account.getPrincipal().getName() );
		assertTrue( account.getRoles().contains( "testable-role" ) );
	}

	@Test
	public void ensureThatCouldNotValidateAdminUser() {
		String user = "admin";
		UsernameAndPasswordCredential credential = new UsernameAndPasswordCredential( user, user );
		Account account = identityManager.verify( credential );
		assertNull( account );
	}

	@Test
	public void ensureThatInvalidCredentialsAreIgnored() {
		X509CertificateCredential credential = new X509CertificateCredential( null );
		Account account = identityManager.verify( credential );
		assertNull( account );
	}
}
