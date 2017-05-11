package kikaha.cloud.aws.ec2;

import java.io.IOException;
import javax.inject.Singleton;
import com.amazonaws.util.EC2MetadataUtils;
import kikaha.cloud.smart.LocalMachineIdentification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: miere.teixeira
 */
@Slf4j
@Singleton
public class AmazonLocalMachineIdentification implements LocalMachineIdentification {

	@Getter(lazy = true)
	private final String machineId = EC2MetadataUtils.getInstanceId();

	@Getter(lazy = true)
	private final String ipAddress = EC2MetadataUtils.getPrivateIpAddress();

	@Override
	public String generateTheMachineId() throws IOException {
		log.info("Retrieving EC2 machine id...");
		return getMachineId();
	}

	@Override
	public String getLocalAddress() throws IOException {
		log.info("Retrieving EC2 machine IP address...");
		return getIpAddress();
	}
}
