package kikaha.cloud.aws;

import java.io.IOException;
import javax.inject.Singleton;
import com.amazonaws.util.EC2MetadataUtils;
import kikaha.cloud.smart.LocalMachineIdentification;
import lombok.Getter;

/**
 * @author: miere.teixeira
 */
@Singleton
public class AmazonLocalMachineIdentification implements LocalMachineIdentification {

	@Getter(lazy = true)
	private final String machineId = EC2MetadataUtils.getInstanceId();


	@Getter(lazy = true)
	private final String ipAddress = EC2MetadataUtils.getPrivateIpAddress();

	@Override
	public String generateTheMachineId() throws IOException {
		return getMachineId();
	}

	@Override
	public String getLocalAddress() throws IOException {
		return getIpAddress();
	}
}
