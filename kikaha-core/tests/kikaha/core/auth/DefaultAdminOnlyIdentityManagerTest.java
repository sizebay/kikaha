package kikaha.core.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.security.idm.X509CertificateCredential;
import kikaha.core.auth.DefaultAdminOnlyIdentityManager;
import lombok.val;

import org.junit.Test;

public class DefaultAdminOnlyIdentityManagerTest {

	final IdentityManager identityManager = new DefaultAdminOnlyIdentityManager();

	@Test
	public void ensureThatCouldValidateAdminUser() {
		val admin = "admin";
		val credential = new PasswordCredential( admin.toCharArray() );
		val account = identityManager.verify( admin, credential );
		assertNotNull( account );
		assertEquals( admin, account.getPrincipal().getName() );
	}

	@Test
	public void ensureThatInvalidCredentialsAreIgnored() {
		val admin = "admin";
		val credential = new X509CertificateCredential( null );
		val account = identityManager.verify( admin, credential );
		assertNull( account );
	}
}
