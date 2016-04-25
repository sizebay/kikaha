package kikaha.mail;

import kikaha.config.Config;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MailSenderProducer {

	@Inject
	Config kikahaConf;

	SimpleMailSender sender;

	@PostConstruct
	public void loadMailSender() {
		final Config config = kikahaConf.getConfig( "server.mail" );
		final MailConfiguration mailConfiguration = MailConfiguration.from( config );
		sender = new SimpleMailSender( mailConfiguration );
	}

	@Produces
	public SimpleMailSender produceMailSender() {
		return sender;
	}
}
