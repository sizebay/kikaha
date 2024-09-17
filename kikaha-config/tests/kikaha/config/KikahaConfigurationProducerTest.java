package kikaha.config;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for KikahaConfigurationProducer.
 */
@RunWith( MockitoJUnitRunner.class )
public class KikahaConfigurationProducerTest {

	KikahaConfigurationProducer producer = new KikahaConfigurationProducer();
	@Mock ConfigEnrichment enrichment;
	@Spy MergeableConfig mergeableConfig;
	@Spy MergeableConfig enrichedMergeableConfig;

	@Before
	public void setupMocks(){
		producer.listOfEnrichment = asList( enrichment );
		producer = spy( producer );
	}

	@Test
	public void ensureThatCanEnrichConfig(){
		doReturn( enrichedMergeableConfig ).when( enrichment ).enrich( any() );
		final Config producedConfig = producer.produceAConfiguration();
		assertEquals( enrichedMergeableConfig, producedConfig );
	}
}