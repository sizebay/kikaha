package kikaha.cloud.aws.ec2;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.ec2.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import lombok.Getter;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AmazonEC2ClientProducer {

	@Inject ClientConfiguration clientConfiguration;

	@Inject @Named("ec2")
	AmazonWebServiceConfiguration configuration;

	@Getter(lazy = true)
	private final AmazonEC2 amazonEC2 = createAmazonEC2Client();

	@Produces
	public AmazonEC2 produceAmazonEC2Client() {
		return getAmazonEC2();
	}

	AmazonEC2 createAmazonEC2Client(){
		return AmazonEC2ClientBuilder.standard()
			.withClientConfiguration( clientConfiguration )
			.withCredentials( configuration.getIamPolicy() )
			.withRegion( configuration.getRegion() )
				.build();
	}
}
