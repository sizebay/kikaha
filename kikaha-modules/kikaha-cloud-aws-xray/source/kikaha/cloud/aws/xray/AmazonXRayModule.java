package kikaha.cloud.aws.xray;

import java.io.IOException;
import javax.inject.*;
import com.amazonaws.xray.AWSXRayRecorder;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Singleton
public class AmazonXRayModule implements Module {

	@Inject AWSXRayRecorder recorder;
	@Inject SegmentFactory segmentFactory;
	@Inject Config config;

	@Override
	public void load( Undertow.Builder server, DeploymentContext context ) throws IOException {
		if ( config.getBoolean("server.aws.x-ray.enabled") ) {
			log.info( "AWS X-Ray enabled." );
			final HttpHandler rootHandler = context.rootHandler();
			final HttpHandler newHandler = new AmazonXRayHttpHandler( recorder, segmentFactory, rootHandler );
			context.rootHandler( newHandler );
		}
	}
}
