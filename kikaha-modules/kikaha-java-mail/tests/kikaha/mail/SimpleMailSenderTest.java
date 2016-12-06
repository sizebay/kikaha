package kikaha.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import kikaha.core.test.KikahaRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

import javax.inject.Inject;

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

	@Test( timeout = 5000 )
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
