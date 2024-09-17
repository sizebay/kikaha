package kikaha.cloud.aws.xray;

import javax.enterprise.inject.Produces;
import javax.inject.*;
import com.amazonaws.xray.*;
import com.amazonaws.xray.contexts.*;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;

/**
 *
 */
@Singleton
public class AmazonXRayProducer {

	private final AWSXRayRecorder recorder = AWSXRayRecorderBuilder.standard()
			.withSegmentContextResolverChain( createSegmentResolverChain() )
			.build();

	@Inject ApplicationData applicationData;

	@Produces
	AWSXRayRecorder produceXRayRecorder(){
		return recorder;
	}

	@Produces
	SegmentNamingStrategy loadSegmentNamingStrategy(){
		final String applicationName = applicationData.getName();
		if ( applicationName.contains("*") )
			return new DynamicSegmentNamingStrategy( applicationName );
		else
			return new FixedSegmentNamingStrategy( applicationName );
	}

	SegmentContextResolverChain createSegmentResolverChain(){
		SegmentContextResolverChain segmentContextResolverChain = new SegmentContextResolverChain();
		segmentContextResolverChain.addResolver(new LambdaSegmentContextResolver());
		return segmentContextResolverChain;
	}
}
