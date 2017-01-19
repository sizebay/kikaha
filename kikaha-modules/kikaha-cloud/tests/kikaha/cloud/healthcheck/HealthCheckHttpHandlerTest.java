package kikaha.cloud.healthcheck;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import javax.inject.Inject;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import kikaha.core.test.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

/**
 * Unit tests for {@link HealthCheckHttpHandler}.
 */
@RunWith( KikahaRunner.class )
public class HealthCheckHttpHandlerTest {

	@Inject HealthCheckHttpHandler httpHandler;
	@Mock HealthCheck healthCheck;
	@Mock Result successResult;
	@Mock Result failureResult;

	@Before
	public void configureMocks(){
		MockitoAnnotations.initMocks( this );
		doReturn( true ).when( successResult ).isHealthy();
		doReturn( false ).when( failureResult ).isHealthy();
		httpHandler.registry.unregister( "SampleDatabaseService" );
	}

	@Test
	public void ensureSendOKStatusWhenServicesAreHealthy() throws Exception {
		doReturn( successResult ).when( healthCheck ).execute();
		simulateHealthCheckDeployment();

		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		run( () -> httpHandler.handleRequest( exchange ) );
		assertEquals( StatusCodes.OK, exchange.getStatusCode() );
	}

	@Test
	public void ensureSendUnavailableStatusWhenServicesAreNotHealthy() throws Exception {
		doReturn( failureResult ).when( healthCheck ).execute();
		simulateHealthCheckDeployment();

		final HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		run( () -> httpHandler.handleRequest( exchange ) );
		assertEquals( StatusCodes.SERVICE_UNAVAILABLE, exchange.getStatusCode() );
	}

	private void simulateHealthCheckDeployment(){
		httpHandler.registry.register( "SampleDatabaseService", healthCheck );
	}

	static void run( RunnableThatMayFail runnable ){
		try {
			runnable.run();
		} catch ( Exception e ) {}
	}

	interface RunnableThatMayFail {
		void run() throws Exception;
	}
}