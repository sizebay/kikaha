package kikaha.urouting.serializers.jackson;

import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import java.io.*;
import io.undertow.security.idm.*;
import io.undertow.server.*;
import kikaha.core.modules.security.*;
import kikaha.core.modules.security.IdentityManager;
import kikaha.core.test.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for JSONAuthenticationMechanism.
 */
@RunWith( KikahaRunner.class )
public class JSONAuthenticationMechanismTest {

	final static String JSON = "{ \"username\": \"user\", \"password\": \"pass\" }";
	final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
	final Credential expectedCredential = new UsernameAndPasswordCredential( "user", "pass" );

	@Inject JSONAuthenticationMechanism mechanism;

	@Mock Jackson jackson;
	@Mock BlockingHttpExchange blockingHttpExchange;
	@Mock IdentityManager identityManager;
	@Mock Account account;

	@Before
	public void configureMocks(){
		MockitoAnnotations.initMocks( this );

		exchange.startBlocking( blockingHttpExchange );
		doReturn(new ByteArrayInputStream( JSON.getBytes() )).when( blockingHttpExchange ).getInputStream();
	}

	@Test
	public void ensureCanReadRequestAndSendUsernameAndPasswordToIdentityManager() throws IOException {
		mechanism.authenticate( exchange, singletonList( identityManager ) );
		verify( identityManager ).verify( Matchers.eq( expectedCredential ) );
	}

	@Test
	public void ensureCanReturnTheAccountOfAuthenticatedUser() throws IOException {
		doReturn( account ).when( identityManager ).verify( Matchers.eq( expectedCredential ) );
		final Account account = mechanism.authenticate( exchange, singletonList( identityManager ) );
		assertNotNull( account );
		assertEquals( account, this.account );
	}
}