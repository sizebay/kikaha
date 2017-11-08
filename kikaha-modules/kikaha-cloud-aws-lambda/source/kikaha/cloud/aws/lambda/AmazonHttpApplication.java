package kikaha.cloud.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import kikaha.core.cdi.CDI;
import kikaha.core.cdi.DefaultCDI;
import kikaha.core.modules.http.WebResource;
import kikaha.core.url.URLMatcher;
import kikaha.urouting.api.Mimes;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 *
 */
@Slf4j @Getter
public class AmazonHttpApplication implements RequestHandler<AmazonLambdaRequest, AmazonLambdaResponse> {

	@Inject @Typed( AmazonHttpHandler.class )
	Iterable<AmazonHttpHandler> amazonHttpHandlers;

	@Inject @Typed( AmazonHttpInterceptor.class )
	Iterable<AmazonHttpInterceptor> interceptors;

	@Inject
    AmazonLambdaSerializer serializer;

	Map<String, List<Entry>> entriesMatcher;

	public AmazonHttpApplication() {
        try {
            log.debug( "Initializing Lambda Application" );
            final CDI cdi = DefaultCDI.newInstance();
            log.debug( "Injecting dependencies..." );
            cdi.injectOn( this );
            log.debug( "Injection process done!" );
            loadHandlers(amazonHttpHandlers);
            log.debug( "Handlers loaded and ready!" );
        } catch ( Throwable cause ) {
            throw handledException( cause );
        }
	}

	@Override
	public AmazonLambdaResponse handleRequest( AmazonLambdaRequest request, Context context ) {
		final AmazonHttpHandler httpHandler = retrieveHttpHandler( request );
		if ( httpHandler == null )
			return AmazonLambdaResponse.notFound();
		try {
			return handleRequest( request, httpHandler );
		} catch ( AmazonLambdaFunctionInterruptedException cause ) {
			return cause.response;
		} catch ( Throwable cause ) {
            return AmazonLambdaResponse.create( 500 )
                .setHeaders( Collections.singletonMap( "Content-Type", Mimes.PLAIN_TEXT ) )
                .setBody( convertToString( cause ) );
		}
	}

	AmazonLambdaResponse handleRequest(AmazonLambdaRequest request, AmazonHttpHandler httpHandler) throws Exception {
		for (AmazonHttpInterceptor hook : interceptors)
			hook.validateRequest(request);
		final AmazonLambdaResponse response = httpHandler.handle(request);
		response.serializeAs(serializer);
		for (AmazonHttpInterceptor hook : interceptors)
			hook.beforeSendResponse(response);
		return response;
	}

	AmazonHttpHandler retrieveHttpHandler( AmazonLambdaRequest request ) {
		final String path = request.getPath().replaceFirst( "/$", "" );
        log.debug( "Retrieving HTTP Handler for request: " + request.httpMethod + " " + path );
		final List<Entry> list = entriesMatcher.getOrDefault( request.getHttpMethod(), Collections.emptyList() );
		for ( final Entry entry : list )
			if ( entry.getMatcher().matches( path, request.pathParameters ) )
				return entry.getHandler();
		return null;
	}

	Map<String, List<Entry>> getEntriesMatcher(){
		return Collections.unmodifiableMap( entriesMatcher );
	}

	void loadHandlers( Iterable<AmazonHttpHandler> handlers ) {
		entriesMatcher = new HashMap<>();

		log.info( "Registering AWS Lambda routes..." );
		for ( AmazonHttpHandler handler : handlers ) {
			final WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
			final List<Entry> entries = entriesMatcher.computeIfAbsent( webResource.method(), k -> new ArrayList<>() );
			final Entry entry = new Entry( webResource.path().replaceFirst( "/$", "" ), webResource.method(), handler );
			log.info( "  > " + entry );
			entries.add( entry );
		}
	}

	RuntimeException handledException( Throwable cause ) {
        return new RuntimeException( cause.getMessage() + "\n" + convertToString(cause), cause );
    }

    String convertToString( Throwable cause ) {
        try (final Writer writer = new StringWriter() ) {
            cause.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

@Value
class Entry {

	Entry( final String url, final String method, final AmazonHttpHandler handler ) {
	    this.asString = method + " " + url;
		this.handler = handler;
		this.matcher = URLMatcher.compile( url, true );
	}

	final String asString;
	final URLMatcher matcher;
	final AmazonHttpHandler handler;

	@Override
	public String toString(){
	    return asString;
    }
}

