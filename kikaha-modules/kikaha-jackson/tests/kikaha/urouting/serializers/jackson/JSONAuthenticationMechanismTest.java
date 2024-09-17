package kikaha.urouting.serializers.jackson;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.*;
import javax.inject.Inject;
import io.undertow.security.idm.*;
import io.undertow.server.*;
import io.undertow.util.*;
import kikaha.core.modules.security.IdentityManager;
import kikaha.core.modules.security.*;
import kikaha.core.test.*;
import kikaha.urouting.api.Mimes;
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
	@Mock Session session;

	@Before
	public void configureMocks(){
		MockitoAnnotations.initMocks( this );

		exchange.setRequestMethod( Methods.POST );
		exchange.setRelativePath( mechanism.formAuthConfiguration.getCallbackUrl() );
		exchange.startBlocking( blockingHttpExchange );
		doReturn(new ByteArrayInputStream( JSON.getBytes() )).when( blockingHttpExchange ).getInputStream();
	}

	@Test
	public void ensureCanReadRequestAndSendUsernameAndPasswordToIdentityManager() throws IOException {
		exchange.setRequestMethod( Methods.POST );
		exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, Mimes.JSON );
		mechanism.authenticate( exchange, singletonList( identityManager ), session );
		verify( identityManager ).verify( Matchers.eq( expectedCredential ) );
	}

	@Test
	public void ensureCanReturnTheAccountOfAuthenticatedUser() throws IOException {
		exchange.setRequestMethod( Methods.POST );
		exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, Mimes.JSON );
		doReturn( account ).when( identityManager ).verify( Matchers.eq( expectedCredential ) );
		final Account account = mechanism.authenticate( exchange, singletonList( identityManager ), session );
		assertNotNull( account );
		assertEquals( account, this.account );
	}

	@Test
	public void ensureWillNotRunTheAuthenticationWhenTheRequestIsNotOfJSONType(){
		exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, null );
		final Account account = mechanism.authenticate( exchange, singletonList( identityManager ), session );
		assertNull( account );
		verify( identityManager, never() ).verify( Matchers.eq( expectedCredential ) );
	}
}