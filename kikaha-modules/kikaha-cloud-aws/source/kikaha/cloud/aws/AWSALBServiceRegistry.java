package kikaha.cloud.aws;

import java.io.IOException;
import javax.inject.*;
import com.amazonaws.*;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.elasticloadbalancing.*;
import com.amazonaws.services.elasticloadbalancing.model.*;
import kikaha.cloud.smart.ServiceRegistry;
import kikaha.config.Config;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
public class AWSALBServiceRegistry implements ServiceRegistry {

	@Inject Config config;
	@Inject ClientConfiguration configuration;
	@Inject AWSCredentialsProducer credentialsProducer;

	@Override
	public void registerIntoCluster(ApplicationData applicationData) throws IOException {
		final String elbName = config.getString("server.aws.alb.smart-server-elb");
		log.info( "Registering " + applicationData.getMachineId() + " to AWS Load Balancer " + elbName );
		final AmazonElasticLoadBalancing client = elbClient();
		final Instance instance = new Instance(applicationData.getMachineId());
		final RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest()
				.withLoadBalancerName(elbName)
				.withInstances(instance);
		final RegisterInstancesWithLoadBalancerResult result = client.registerInstancesWithLoadBalancer(request);
		logResult( "Registering into Load Balancer " + elbName, result.getSdkResponseMetadata() );
		if (result.getSdkHttpMetadata().getHttpStatusCode() != 200)
			throw new IOException("Could not register application to the Load Balancer named '"+elbName+"'");
	}

	@Override
	public void deregisterFromCluster( ApplicationData applicationData ) throws IOException {
		final String elbName = config.getString("server.aws.alb.smart-server-elb");
		final AmazonElasticLoadBalancing client = elbClient();
		final Instance instance = new Instance(applicationData.getMachineId());
		final DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest()
				.withLoadBalancerName(elbName)
				.withInstances(instance);
		final DeregisterInstancesFromLoadBalancerResult result = client.deregisterInstancesFromLoadBalancer(request);
		logResult( "Deregistering from Load Balancer " + elbName, result.getSdkResponseMetadata() );
		if (result.getSdkHttpMetadata().getHttpStatusCode() != 200)
			throw new IOException("Could not register application to the Load Balancer named '"+elbName+"'");
	}

	private void logResult( final String event, final ResponseMetadata sdkResponseMetadata ) {
		log.info( event + ". Request id: " + sdkResponseMetadata.getRequestId() + ": " + sdkResponseMetadata.toString() );
	}

	private AmazonElasticLoadBalancing elbClient(){
		final String credentialName = config.getString("server.aws.alb.default-credential");
		final AWSCredentialsProvider credentialProvider = credentialsProducer.getCredentialProvider(credentialName);
		return AmazonElasticLoadBalancingClientBuilder.standard()
			.withCredentials( credentialProvider )
			.withClientConfiguration( configuration )
			.build();
	}
}
