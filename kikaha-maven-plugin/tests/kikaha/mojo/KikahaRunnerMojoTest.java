package kikaha.mojo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 */
public class KikahaRunnerMojoTest {

	String jvmArgs = "\n" +
			"                        -Dserver.aws.ec2.tag-as-config-enabled=false\n" +
			"                        -Dserver.smart-server.service-registry=kikaha.cloud.smart.DummyServiceRegistry\n" +
			"                   ";

	@Test
	public void getJvmArgs() throws Exception {
		final KikahaRunnerMojo kikahaRunnerMojo = new KikahaRunnerMojo();
		kikahaRunnerMojo.jvmArgs = jvmArgs;
		assertEquals(
			"-Dserver.aws.ec2.tag-as-config-enabled=false -Dserver.smart-server.service-registry=kikaha.cloud.smart.DummyServiceRegistry",
			kikahaRunnerMojo.getJvmArgs() );
	}

}