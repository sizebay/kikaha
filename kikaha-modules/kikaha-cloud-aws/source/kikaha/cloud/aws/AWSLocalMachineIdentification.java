package kikaha.cloud.aws;

import javax.inject.*;
import java.io.IOException;
import java.util.List;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import kikaha.cloud.smart.LocalMachineIdentification;
import lombok.Getter;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AWSLocalMachineIdentification implements LocalMachineIdentification {

	@Inject AmazonEC2Client client;

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
