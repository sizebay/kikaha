package kikaha.cloud.aws.ec2;

import java.util.*;
import java.util.function.Function;
import javax.inject.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import kikaha.config.*;

/**
 * Enriches the {@link Config} with Tags attached to the current EC2 instance.
 */
@Singleton
public class AmazonEC2ConfigEnrichment implements ConfigEnrichment {

	@Inject AmazonLocalMachineIdentification identification;
	@Inject AmazonEC2 ec2;

	@Override
	public Config enrich(Config originalConfig) {
		final List<Tag> tags = getInstanceTags(identification.getMachineId());
		final Map<String, String> tagsAsMap = toMap(tags, t -> t.getKey(), t -> t.getValue());
		return new AmazonEC2Config( tagsAsMap, originalConfig );
	}

	private List<Tag> getInstanceTags(String instanceId) {
		final List<Tag> tags = new ArrayList<>();
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(Arrays.asList(instanceId));
		final DescribeInstancesResult response = ec2.describeInstances( request );
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
