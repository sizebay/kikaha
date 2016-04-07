package kikaha.core.url;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SimpleRoutingHandler implements HttpHandler {

	final Map<HttpString, List<Entry>> matchersByMethod = new HashMap<>();

	@NonNull
	volatile HttpHandler fallbackHandler;

	public synchronized void add( final String method, final String url, final HttpHandler handler ) {
		final HttpString methodAsHttpString = new HttpString( method );
		add( methodAsHttpString, url, handler );
	}

	public void add( final HttpString method, final String url, final HttpHandler handler ) {
		List<Entry> list = matchersByMethod.get( method );
		if ( list == null )
			matchersByMethod.put( method, list = new ArrayList<>() );
		if ( retrieveEntryThatMatchesUrlFromList( list, url ) != null )
			throw new UnsupportedOperationException( "Already exists a route defined for " + url );
		list.add( new Entry( url, handler ) );
	}

	MatchedEntry retrieveEntryThatMatchesUrlFromList( final List<Entry> list, final String url ) {
		final Map<String, String> matchedPathParameters = new HashMap<>();
		for ( final Entry entry : list )
			if ( entry.getMatcher().matches( url, matchedPathParameters ) )
				return new MatchedEntry( url, entry.getHandler(), matchedPathParameters );
		return null;
	}

	// UNCHECKED: It throws Exception because it implements a superinterface method
	@Override
	public void handleRequest( final HttpServerExchange exchange ) throws Exception {
	// CHECKED
		final HttpString method = exchange.getRequestMethod();
		final List<Entry> list = matchersByMethod.get( method );

		if ( list != null ) {
			final String relativePath = exchange.getRelativePath();
			final MatchedEntry entry = retrieveEntryThatMatchesUrlFromList( list, relativePath );
			if ( entry != null ) {
				handleRequest( exchange, entry );
				return;
			}
		}

		fallbackHandler.handleRequest( exchange );
	}

	void handleRequest( final HttpServerExchange exchange, final MatchedEntry entry ) throws Exception {
		final PathTemplateMatch templateMatch = new PathTemplateMatch( entry.getUrl(), entry.getMatchedPathParameters() );
		exchange.putAttachment( PathTemplateMatch.ATTACHMENT_KEY, templateMatch );
		entry.getHandler().handleRequest( exchange );
	}
}

@Value
class Entry {

	public Entry( final String url, final HttpHandler handler ) {
		this.handler = handler;
		this.matcher = URLMatcher.compile( url );
	}

	final URLMatcher matcher;
	final HttpHandler handler;
}

@Value
class MatchedEntry {

	final String url;
	final HttpHandler handler;
	final Map<String, String> matchedPathParameters;
}