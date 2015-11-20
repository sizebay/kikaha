package kikaha.mail;

import javax.annotation.PostConstruct;

import com.typesafe.config.Config;

import kikaha.core.api.conf.Configuration;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
public class MailSenderProducer {

	@Provided
	Configuration kikahaConf;

	SimpleMailSender sender;

	@PostConstruct
	public void loadMailSender() {
		final Config config = kikahaConf.config().getConfig( "server.mail" );
		final MailConfiguration mailConfiguration = MailConfiguration.from( config );
		sender = new SimpleMailSender( mailConfiguration );
	}

	@Producer
	public SimpleMailSender produceMailSender() {
		return sender;
	}
}
