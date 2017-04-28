package kikaha.cloud.aws.ec2;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.ec2.*;
import kikaha.cloud.aws.iam.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import kikaha.core.cdi.ProviderContext;
import lombok.NonNull;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AmazonEC2ClientProducer {

	@Inject ClientConfiguration configuration;
	@Inject AmazonConfigurationProducer configurationProducer;

	@Produces
	public AmazonEC2 produceAmazonEC2Client( final ProviderContext context ) {
		final IAM annotation = context.getAnnotation(IAM.class);
		final String configurationName = annotation != null ? annotation.value() : "default";
		return produceAmazonEC2Client( configurationName );
	}

	public AmazonEC2 produceAmazonEC2Client( @NonNull final String alias ){
		final AmazonWebServiceConfiguration configuration = configurationProducer.configForService(alias);
		return AmazonEC2ClientBuilder.standard()
			.withClientConfiguration(this.configuration)
			.withCredentials( configuration.getIamPolicy() )
			.withRegion( configuration.getRegion() )
				.build();
	}
}
