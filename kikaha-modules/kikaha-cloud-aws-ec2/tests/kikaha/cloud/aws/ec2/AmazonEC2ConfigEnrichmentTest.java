package kikaha.cloud.aws.ec2;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import kikaha.cloud.aws.iam.AmazonCredentialsFactory;
import kikaha.config.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link AmazonEC2ConfigEnrichment}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonEC2ConfigEnrichmentTest {

	final Config config = ConfigLoader.loadDefaults();

	@InjectMocks @Spy AmazonEC2ConfigEnrichment enrichment;

	@Mock AmazonEC2 ec2;
	@Mock AmazonCredentialsFactory.Default defaultCredentialFactory;
	@Mock AmazonEC2MachineIdentification identification;

	@Before
	public void configureInstanceTagsOnMocks(){
		doReturn( ec2 ).when(enrichment).loadEC2Client();

		final Instance instance = new Instance().withTags(
			new Tag().withKey("tag.from.ec2").withValue("true"),
			new Tag().withKey("server.static.enabled").withValue("true"));
		doReturn( new DescribeInstancesResult().withReservations( new Reservation().withInstances(instance) ) )
			.when( ec2 ).describeInstances( any() );
	}

	@Test
	public void ensureCanRetrieveNewTagValuesFromEC2(){
		final Config config = enrichment.enrich(this.config);
		assertEquals( "true", config.getString("tag.from.ec2") );
		assertTrue( config.getBoolean("tag.from.ec2") );
	}

	@Test
	public void ensureCanRetrieveAnOverridenValueFromEC2(){
		final Config config = enrichment.enrich(this.config);
		assertEquals( "true", config.getString("server.static.enabled") );
		assertTrue( config.getBoolean("server.static.enabled") );
	}

	@Test
	public void ensureCanRetrieveOlderValuesFromConfig(){
		assertNull(this.config.getString("tag.from.ec2") );
		assertFalse( this.config.getBoolean("server.static.enabled") );
	}
}