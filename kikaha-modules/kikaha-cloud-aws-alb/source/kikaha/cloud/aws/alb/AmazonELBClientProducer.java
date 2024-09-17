package kikaha.cloud.aws.alb;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.elasticloadbalancingv2.*;
import kikaha.cloud.aws.iam.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import lombok.Getter;

/**
 *
 */
@Singleton
public class AmazonELBClientProducer {

	@Inject
	ClientConfiguration clientConfiguration;

	@Inject
	@Named( "elb" )
	AmazonWebServiceConfiguration amazonWebServiceConfiguration;

	@Getter(lazy = true)
	private final AmazonElasticLoadBalancing elbClient = elbClient();

	@Produces
	public AmazonElasticLoadBalancing produceElbClient(){
		return getElbClient();
	}

	AmazonElasticLoadBalancing elbClient(){
		return AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials( amazonWebServiceConfiguration.getIamPolicy() )
				.withRegion( amazonWebServiceConfiguration.getRegion() )
				.withClientConfiguration( clientConfiguration )
				.build();
	}
}
