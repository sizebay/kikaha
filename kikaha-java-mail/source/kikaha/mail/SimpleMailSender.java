package kikaha.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SimpleMailSender {

	final Session session;
	final MailConfiguration mailConf;

	public SimpleMailSender( MailConfiguration mailConf ) {
		this.mailConf = mailConf;
		session = createSession();
	}

	public void sendMail( String to, String subject, String content ) {
		try {
			log.debug( "Sending mail to (" + to + "): " + subject );
			final Message message = createMessage( to, mailConf.defaultSender );
			message.setSubject( subject );
			message.setContent( content, "text/html" );
			sendMessage( message );
		} catch ( final Throwable cause ) {
			throw new RuntimeException( cause );
		}
	}

	private Message createMessage( String to, String from )
			throws MessagingException, AddressException
	{
		final Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( from ) );
		message.setRecipients( Message.RecipientType.BCC, InternetAddress.parse( to ) );
		return message;
	}

	private void sendMessage( Message message ) throws MessagingException {
		final Transport transport = session.getTransport();
		transport.connect( mailConf.host, mailConf.port, mailConf.username, mailConf.password );
		transport.sendMessage( message, message.getAllRecipients() );
		transport.close();
	}

	private Session createSession() {
		return Session.getInstance(
				createConnectionProperties(), null );
	}

	private Properties createConnectionProperties() {
		final Properties props = new Properties();
		props.put( "mail.transport.protocol", mailConf.protocol );
		props.put( "mail.smtp.auth", String.valueOf( mailConf.getUseAuth() ) );
		props.put( "mail.smtp.starttls.enable", String.valueOf( mailConf.getUseTLS() ) );
		props.put( "mail.debug", String.valueOf( log.isDebugEnabled() ) );
		return props;
	}
}