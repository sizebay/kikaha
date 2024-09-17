package kikaha.cloud.aws.xray;

import static com.amazonaws.xray.entities.TraceHeader.SampleDecision.*;
import static io.undertow.util.Headers.*;
import java.util.HashMap;
import javax.inject.*;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.*;
import com.amazonaws.xray.entities.TraceHeader.SampleDecision;
import com.amazonaws.xray.strategy.sampling.SamplingStrategy;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import kikaha.cloud.smart.ServiceRegistry.ApplicationData;

/**
 *
 */
@Singleton
public class SegmentFactory {

	static final HttpString X_AMZN_TRACE_ID = new HttpString("X-Amzn-Trace-Id");

	@Inject AWSXRayRecorder recorder;
	@Inject SegmentNamingStrategy segmentNamingStrategy;
	@Inject ApplicationData applicationData;

	public Segment createSegment(HttpServerExchange exchange) throws Exception {
		final String traceHeaderString = exchange.getRequestHeaders().getFirst( TraceHeader.HEADER_KEY );
		final TraceHeader traceHeader = traceHeaderString != null ? TraceHeader.fromString( traceHeaderString ) : null;
		final SampleDecision decision = getSampleDecision(traceHeader, exchange);

		final Segment segment = createSegment(applicationData.getName(), decision, traceHeader);
		attachTraceId( exchange, traceHeader, segment );

		final HashMap<String, Object> requestAttributes = createRequestAttributes(exchange);
		segment.putHttp("request", requestAttributes);

		return segment;
	}

	SampleDecision getSampleDecision(TraceHeader traceHeader, HttpServerExchange exchange ) {
		SampleDecision decision = UNKNOWN;
		if ( traceHeader != null )
			decision = traceHeader.getSampled();

		if ( UNKNOWN.equals(decision) || REQUESTED.equals(decision) )
		{
			final boolean shouldTrace = recorder.getSamplingStrategy().shouldTrace(
					exchange.getHostName(), exchange.getRelativePath(),
					exchange.getRequestMethod().toString());
			decision = shouldTrace ? SAMPLED : NOT_SAMPLED;
		}

		return decision;
	}

	Segment createSegment( String host, SampleDecision decision, TraceHeader traceHeader ){
		final SamplingStrategy samplingStrategy = this.recorder.getSamplingStrategy();
		final String parentId = traceHeader != null ? traceHeader.getParentId() : null;
		final TraceID traceId = traceHeader != null ? traceHeader.getRootTraceId() : new TraceID();

		Segment created;
		if( SAMPLED.equals(decision) )
			created = beginSegmentFrom( host, traceId, parentId );
		else if( samplingStrategy.isForcedSamplingSupported() ) {
			created = beginSegmentFrom(host, traceId, parentId );
			created.setSampled(false);
		} else
			created = beginDummySegment(host,traceId);

		return created;
	}

	private Segment beginSegmentFrom( String host, TraceID traceId, String parentId ) {
		final String segmentName = this.getSegmentName(host);
		final SegmentImpl segment = new SegmentImpl(this.recorder, segmentName, traceId);
		segment.setParentId( parentId );
		return segment;
	}

	Segment beginDummySegment(String host, TraceID traceId) {
		return new SegmentImpl(this.recorder, host, traceId);
	}

	private String getSegmentName(String host) {
		try {
			return this.segmentNamingStrategy.nameForRequest(host);
		} catch (NullPointerException var3) {
			throw new RuntimeException("kikaha-cloud-aws-xray requires either a fixedName configuration or a SegmentNamingStrategy be provided. Please change your application.yml or constructor call as necessary.", var3);
		}
	}

	private HashMap<String, Object> createRequestAttributes( HttpServerExchange exchange ){
		HashMap<String, Object> requestAttributes = new HashMap<>();
		requestAttributes.put("url", exchange.getRelativePath() );
		requestAttributes.put("method", exchange.getRequestMethod().toString());

		final String userAgent = exchange.getRequestHeaders().getFirst(USER_AGENT);
		if(userAgent != null)
			requestAttributes.put("user_agent", userAgent);

		final String xForwardedFor = exchange.getRequestHeaders().getFirst(X_FORWARDED_FOR);
		if(xForwardedFor != null) {
			requestAttributes.put("client_ip", xForwardedFor);
			requestAttributes.put("x_forwarded_for", Boolean.valueOf(true));
		} else {
			final String clientIp = exchange.getDestinationAddress().getHostString();
			requestAttributes.put("client_ip", clientIp);
		}

		return requestAttributes;
	}

	private void attachTraceId( HttpServerExchange exchange, TraceHeader traceHeader, Segment created ){
		TraceHeader responseHeader;

		if(traceHeader != null) {
			responseHeader = new TraceHeader( traceHeader.getRootTraceId() );
			if( SampleDecision.REQUESTED == traceHeader.getSampled() ) {
				final SampleDecision newDecision = created.isSampled()? SampleDecision.SAMPLED : SampleDecision.NOT_SAMPLED;
				responseHeader.setSampled(newDecision);
			}
		} else
			responseHeader = new TraceHeader(created.getTraceId());

		exchange.getResponseHeaders().put(X_AMZN_TRACE_ID, responseHeader.toString());
	}
}
