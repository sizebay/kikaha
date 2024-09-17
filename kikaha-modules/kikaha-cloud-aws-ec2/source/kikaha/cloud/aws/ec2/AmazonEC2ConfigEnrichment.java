package kikaha.cloud.aws.ec2;

import static java.util.Collections.singletonList;
import java.util.*;
import java.util.function.Function;
import javax.inject.*;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import kikaha.cloud.aws.iam.AmazonCredentialsFactory;
import kikaha.config.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enriches the {@link Config} with Tags attached to the current EC2 instance.
 */
@Slf4j
@Singleton
public class AmazonEC2ConfigEnrichment implements ConfigEnrichment {

	@Inject AmazonEC2MachineIdentification identification;
	@Inject AmazonCredentialsFactory.Default defaultCredentialFactory;

	@Getter(lazy = true)
	private final AmazonEC2 ec2 = loadEC2Client();

	AmazonEC2 loadEC2Client() {
		return AmazonEC2ClientBuilder.standard()
			.withCredentials( defaultCredentialFactory.loadCredentialProvider() )
			.withRegion( identification.getRegion() )
			.build();
	}

	@Override
	public Config enrich(Config originalConfig) {
		if ( !originalConfig.getBoolean( "server.aws.ec2.tag-as-config-enabled" ) )
			return originalConfig;

		log.info( "Using the tags associated with this EC2 instance as configuration entries." );
		return new AmazonEC2Config( this::getInstanceTagsAsMap, originalConfig );
	}

	private Map<String, String> getInstanceTagsAsMap(){
		try {
			final List<Tag> tags = getInstanceTags(identification.getMachineId());
			final Map<String, String> tagsAsMap = toMap(tags, Tag::getKey, Tag::getValue);
			log.debug( "Found EC2 tags: " + tagsAsMap );
			return tagsAsMap;
		} catch ( Throwable cause ) {
			log.error( "Could not retrieve EC2 tags", cause );
			return Collections.emptyMap();
		}
	}

	private List<Tag> getInstanceTags(String instanceId) {
		final List<Tag> tags = new ArrayList<>();
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(singletonList(instanceId));
		final DescribeInstancesResult response = getEc2().describeInstances( request );
		for (final Reservation res : response.getReservations())
			for (final Instance inst : res.getInstances()) {
				final List<Tag> instanceTags = inst.getTags();
				if (instanceTags != null && instanceTags.size() > 0)
					tags.addAll(instanceTags);
			}
		return tags;
	}

	private <T,K,V> Map<K,V> toMap(List<T> listOfT, Function<T,K> keyMapper,Function<T,V> valueMapper) {
		final Map<K,V> map = new HashMap<>();

		for ( T t : listOfT )
			map.put(
				keyMapper.apply(t),
				valueMapper.apply(t)
			);

		return map;
	}
}
