package kikaha.uworkers;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import kikaha.core.test.KikahaRunner;
import kikaha.uworkers.api.*;
import kikaha.uworkers.core.MicroWorkersTaskDeploymentModule;
import org.elasticmq.rest.sqs.*;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class SQSMessageITest {

	SQSRestServer sqsRestServer;

	@Inject MicroWorkersTaskDeploymentModule module;
	@Inject SQSSampleEndpointListener listener;
	@Worker("sample-msg") WorkerRef ref;

	@Before
	public void start() throws IOException {
		module.load( null, null );
		sqsRestServer = SQSRestServerBuilder.withPort(9325).withInterface("localhost").start();
	}

	@After
	public void shutdown(){
		module.unload();
		sqsRestServer.stopAndWait();
	}

	@Test
	public void ensureCanSendAndReceiveMessages() throws IOException, InterruptedException {
		final SQSSampleEndpointListener.Message message = new SQSSampleEndpointListener.Message();
		ref.send( message );
		Thread.sleep( 1000 );
		assertEquals( message, listener.message );
	}
}
