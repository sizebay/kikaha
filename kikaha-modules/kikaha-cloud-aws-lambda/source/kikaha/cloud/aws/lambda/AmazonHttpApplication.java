package kikaha.cloud.aws.lambda;

import java.util.*;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import com.amazonaws.services.lambda.runtime.*;
import kikaha.core.cdi.*;
import kikaha.core.modules.http.WebResource;
import kikaha.core.url.URLMatcher;
import lombok.Value;

/**
 *
 */
public class AmazonHttpApplication implements RequestHandler<AmazonLambdaRequest, AmazonLambdaResponse> {

	final CDI cdi = DefaultCDI.newInstance();

	@Inject @Typed( AmazonHttpHandler.class )
	Iterable<AmazonHttpHandler> amazonHttpHandlers;

	@Inject @Typed( AmazonHttpHandler.class )
	Iterable<AmazonHttpResponseHook> responseHooks;

	Map<String, List<Entry>> entriesMatcher;

	public AmazonHttpApplication() {
		cdi.injectOn( this );
		loadHandlers(amazonHttpHandlers);
	}

	@Override
	public AmazonLambdaResponse handleRequest( AmazonLambdaRequest request, Context context ) {
		final AmazonHttpHandler httpHandler = retrieveHttpHandler( request );
		if ( httpHandler == null )
			return AmazonLambdaResponse.notFound();
		final AmazonLambdaResponse response = httpHandler.handle(request);
		for ( AmazonHttpResponseHook hook : responseHooks )
			hook.apply( request, response );
		return response;
	}

	AmazonHttpHandler retrieveHttpHandler( AmazonLambdaRequest request ) {
		final String path = request.getPath().replaceFirst( "/$", "" );
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

		for ( AmazonHttpHandler handler : handlers ) {
			final WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
			final List<Entry> entries = entriesMatcher.computeIfAbsent( webResource.method(), k -> new ArrayList<>() );
			entries.add( new Entry( webResource.path().replaceFirst( "/$", "" ), handler ) );
		}
	}
}

@Value
class Entry {

	Entry( final String url, final AmazonHttpHandler handler ) {
		this.handler = handler;
		this.matcher = URLMatcher.compile( url, true );
	}

	final URLMatcher matcher;
	final AmazonHttpHandler handler;
}

