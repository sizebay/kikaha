package kikaha.cloud.aws;

import static org.junit.Assert.*;
import javax.inject.*;
import com.amazonaws.auth.*;
import kikaha.core.test.KikahaRunner;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class AmazonCredentialsProducerTest {

	@Inject AWSCredentials producedCredentials;

	@IAM( "alternative" )
	@Inject AWSCredentials alternativeProducedCredentials;

	@IAM( "default" )
	@Inject AWSCredentials defaultProducedCredentials;

	@Test
	public void canInjectDefaultAWSCredentialsWithCredentialsFoundAtConfigurationForUnnamedInjectionPoint(){
		assertNotNull( producedCredentials );
		assertTrue( BasicAWSCredentials.class.isInstance( producedCredentials ));

		final BasicAWSCredentials basicAWSCredentials = (BasicAWSCredentials)producedCredentials;
		assertEquals( "1234", basicAWSCredentials.getAWSAccessKeyId() );
		assertEquals( "4321", basicAWSCredentials.getAWSSecretKey() );
	}

	@Test
	public void canInjectDefaultAWSCredentialsWithCredentialsFoundAtConfiguration(){
		assertNotNull( defaultProducedCredentials );
		assertTrue( BasicAWSCredentials.class.isInstance( defaultProducedCredentials ));

		final BasicAWSCredentials basicAWSCredentials = (BasicAWSCredentials)defaultProducedCredentials;
		assertEquals( "1234", basicAWSCredentials.getAWSAccessKeyId() );
		assertEquals( "4321", basicAWSCredentials.getAWSSecretKey() );
	}

	@Test
	public void ensureInjectedUnnamedCredentialsAreEqualsToDefaultOne(){
		assertEquals( producedCredentials, defaultProducedCredentials );
		assertSame( producedCredentials, defaultProducedCredentials );
	}

	@Test
	public void canInjectAlternativeAWSCredentialsWithCredentialsFoundAtConfiguration(){
		assertNotNull( alternativeProducedCredentials );
		assertTrue( BasicAWSCredentials.class.isInstance( alternativeProducedCredentials ));

		final BasicAWSCredentials basicAWSCredentials = (BasicAWSCredentials)alternativeProducedCredentials;
		assertEquals( "2345", basicAWSCredentials.getAWSAccessKeyId() );
		assertEquals( "5432", basicAWSCredentials.getAWSSecretKey() );
	}
}