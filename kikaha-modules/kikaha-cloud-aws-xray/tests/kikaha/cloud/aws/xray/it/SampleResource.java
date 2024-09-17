package kikaha.cloud.aws.xray.it;

import kikaha.cloud.smart.tracer.TraceId;
import kikaha.urouting.api.Context;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Path;

import javax.inject.Singleton;

/**
 * Created by miere.teixeira on 16/06/2017.
 */
@Singleton
@Path("api/sample")
public class SampleResource {

    @GET
    @Path("trace-id")
    public String getTraceId( @Context TraceId traceId ){
        return traceId.id();
    }
}
