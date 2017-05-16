package kikaha.cloud.aws.cloudwatch;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.cloudwatch.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import lombok.Getter;

/**
 *
 */
@Singleton
public class Producers {

	@Inject ClientConfiguration clientConfiguration;

	@Inject @Named("cloudwatch")
	AmazonWebServiceConfiguration cloudWatchConf;

	@Getter( lazy = true )
	private final AmazonCloudWatch amazonCloudWatch = createCloudWatchClient();

	@Produces AmazonCloudWatch produceAmazonCloudWatch(){
		return getAmazonCloudWatch();
	}

	AmazonCloudWatch createCloudWatchClient(){
		return AmazonCloudWatchClientBuilder.standard()
			.withCredentials( cloudWatchConf.getIamPolicy() )
			.withClientConfiguration( clientConfiguration )
			.withRegion( cloudWatchConf.getRegion() )
				.build();
	}
}
