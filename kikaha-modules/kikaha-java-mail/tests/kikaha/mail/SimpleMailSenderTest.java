package kikaha.mail;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import kikaha.core.test.KikahaRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith( KikahaRunner.class )
public class SimpleMailSenderTest {

	@Inject
	SimpleMailSender sender;

	SimpleSmtpServer server;

	@Before
	public void startStubEmailService() {
		server = SimpleSmtpServer.start( 2500 );
	}

	@After
	public void stopStubEmailService() {
		server.stop();
	}

	@Test( timeout = 15000 )
	@SuppressWarnings( "unchecked" )
	public void ensureThatCanSendEmail() {
		sender.sendMail( "you@about.you", "Hello", "World!" );
		assertTrue( server.getReceivedEmailSize() == 1 );

		final Iterator<SmtpMessage> emailIter = server.getReceivedEmail();
		final SmtpMessage email = emailIter.next();
		assertEquals( "Hello", email.getHeaderValue( "Subject" ) );
		assertEquals( "World!", email.getBody() );
	}
}
