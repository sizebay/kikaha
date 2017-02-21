package kikaha.cloud.aws;

import java.io.IOException;
import javax.inject.*;
import com.amazonaws.*;
import com.amazonaws.services.elasticloadbalancing.*;
import com.amazonaws.services.elasticloadbalancing.model.*;
import kikaha.cloud.aws.AmazonConfigurationProducer.AmazonWebServiceConfiguration;
import kikaha.cloud.smart.ServiceRegistry;
import kikaha.config.Config;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
public class AmazonELBServiceRegistry implements ServiceRegistry {

	@Inject Config config;
	@Inject ClientConfiguration configuration;
	@Inject AmazonConfigurationProducer configurationProducer;

	@Override
	public void registerIntoCluster(ApplicationData applicationData) throws IOException {
		final String elbName = config.getString("server.aws.elb.auto-join-to");
		if ( elbName == null )
			throw new IOException( "Could not automatically join to the AWS Load Balancer named 'null'" );

		log.info( "Registering " + applicationData.getMachineId() + " to AWS Load Balancer " + elbName );
		final Instance instance = new Instance(applicationData.getMachineId());
		final RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest()
				.withLoadBalancerName(elbName)
				.withInstances(instance);

		final RegisterInstancesWithLoadBalancerResult result = elbClient().registerInstancesWithLoadBalancer(request);
		logResult( "Registering into Load Balancer " + elbName, result.getSdkResponseMetadata() );
		if (result.getSdkHttpMetadata().getHttpStatusCode() != 200)
			throw new IOException("Could not automatically join to AWS Load Balancer named '"+elbName+"'");
	}

	@Override
	public void deregisterFromCluster( ApplicationData applicationData ) throws IOException {
		final String elbName = config.getString("server.aws.elb.auto-join-to");
		if ( elbName == null )
			throw new IOException( "Could not automatically join to the AWS Load Balancer named 'null'" );

		final Instance instance = new Instance(applicationData.getMachineId());
		final DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest()
				.withLoadBalancerName(elbName)
				.withInstances(instance);

		final DeregisterInstancesFromLoadBalancerResult result = elbClient().deregisterInstancesFromLoadBalancer(request);
		logResult( "Deregistering from Load Balancer " + elbName, result.getSdkResponseMetadata() );
		if (result.getSdkHttpMetadata().getHttpStatusCode() != 200)
			throw new IOException("Could not leave the AWS Load Balancer named '"+elbName+"'");
	}

	private void logResult( final String event, final ResponseMetadata sdkResponseMetadata ) {
		log.info( event + ". Request id: " + sdkResponseMetadata.getRequestId() + ": " + sdkResponseMetadata.toString() );
	}

	private AmazonElasticLoadBalancing elbClient(){
		final AmazonWebServiceConfiguration configuration = configurationProducer.configForService("elb");
		return AmazonElasticLoadBalancingClientBuilder.standard()
			.withCredentials( configuration.getIamPolicy() )
			.withRegion( configuration.getRegion() )
			.withClientConfiguration( this.configuration )
				.build();
	}
}
