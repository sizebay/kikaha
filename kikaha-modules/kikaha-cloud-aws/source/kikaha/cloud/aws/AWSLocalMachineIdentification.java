package kikaha.cloud.aws;

import java.io.IOException;
import java.util.List;
import javax.inject.*;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import kikaha.cloud.smart.LocalMachineIdentification;
import kikaha.config.Config;
import lombok.Getter;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AWSLocalMachineIdentification implements LocalMachineIdentification {

	@Inject Config config;
	@Inject AmazonEC2Client client;
	@Inject AWSCredentialsProducer credentialsProducer;

	@Getter(lazy = true)
	private final String machineId = EC2MetadataUtils.getInstanceId();

	@Override
	public String generateTheMachineId() throws IOException {
		return getMachineId();
	}

	@Override
	public String getLocalAddress() throws IOException {
		final DescribeInstancesRequest describeInstances = new DescribeInstancesRequest().withInstanceIds( getMachineId() );
		return client.describeInstances( describeInstances )
				.getReservations().stream()
				.map( Reservation::getInstances)
				.flatMap( List::stream ).findFirst()
				.map( Instance::getPublicIpAddress)
				.orElse(null);
	}
}
