package kikaha.cloud.aws.ec2;

import java.io.IOException;
import javax.inject.Singleton;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.EC2MetadataUtils;
import kikaha.cloud.smart.MachineIdentification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: miere.teixeira
 */
@Slf4j
@Singleton
public class AmazonEC2MachineIdentification implements MachineIdentification {

	@Getter(lazy = true)
	private final String machineId = EC2MetadataUtils.getInstanceId();

	@Getter(lazy = true)
	private final String ipAddress = EC2MetadataUtils.getPrivateIpAddress();

	@Getter(lazy = true)
	private final Regions region = Regions.fromName( EC2MetadataUtils.getEC2InstanceRegion() );

	@Override
	public String generateTheMachineId() throws IOException {
		log.debug("Retrieving EC2 machine id...");
		return getMachineId();
	}

	@Override
	public String getLocalAddress() throws IOException {
		log.debug("Retrieving EC2 machine IP address...");
		return getIpAddress();
	}
}
