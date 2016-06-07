package kikaha.core.modules.websocket;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import java.util.*;
import java.util.concurrent.*;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import javax.inject.*;
import io.undertow.*;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.modules.Module;
import kikaha.core.modules.http.*;
import kikaha.core.url.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * A module that deploy {@link WebSocketHandler}s.
 */
@Slf4j
@Singleton
public class WebSocketModule implements Module {

	@Getter
	final String name = "websocket";

	@Inject
	@Typed( WebSocketHandler.class )
	Iterable<WebSocketHandler> handlers;

	@Inject
	@Typed( WebSocketSession.Serializer.class )
	Collection<WebSocketSession.Serializer> webSocketSerializers;

	@Inject
	@Typed( WebSocketSession.Unserializer.class )
	Collection<WebSocketSession.Unserializer> webSocketUnserializers;

	@Inject
	Config config;

	@NonNull @Getter
	WebSocketSession.Serializer serializer;

	@NonNull @Getter
	WebSocketSession.Unserializer unserializer;

	ExecutorService executorService;

	@PostConstruct
	public void configureModule(){
		loadSerializersAndUnserializers();
		loadWorkersThreadPool();
	}

	private void loadSerializersAndUnserializers(){
		final Config webSocketConfig = config.getConfig( "server.websocket" );
		final Map<String, WebSocketSession.Serializer> serializers = webSocketSerializers.stream().collect(toMap(this::extractContentType, identity()));
		serializer = serializers.get( webSocketConfig.getString("default-serializer") );
		final Map<String, WebSocketSession.Unserializer> unserializers = webSocketUnserializers.stream().collect(toMap(this::extractContentType, identity()));
		unserializer = unserializers.get( webSocketConfig.getString("default-unserializer") );
	}

	public void loadWorkersThreadPool(){
		final int size = config.getInteger("server.websocket.worker-threads");
		if ( size > 0 )
			executorService = Executors.newFixedThreadPool( size );
		else executorService = Executors.newCachedThreadPool();
	}

	String extractContentType( Object object ) {
		final Class<?> clazz = object.getClass();
		final ContentType annotation = clazz.getAnnotation(ContentType.class);
		if ( annotation == null ){
			final String msg = clazz + " should be annotated with @" + ContentType.class;
			throw new UnsupportedOperationException( msg );
		}
		return annotation.value();
	}

	@Override
	public void load( Undertow.Builder server, final DeploymentContext context ) {
		for ( final WebSocketHandler handler : handlers )
			deploy( context, handler );
	}

	void deploy( final DeploymentContext context, final WebSocketHandler handler ) {
		final WebResource webResource = handler.getClass().getAnnotation( WebResource.class );
		if ( webResource == null ) {
			log.warn( "No WebResource annotation found for " + handler.getClass().getCanonicalName() + ": Skipped!" );
			return;
		}
		context.register( webResource.path(), "GET", wrappedWebsocketHandlerFrom( handler, webResource ) );
	}

	HttpHandler wrappedWebsocketHandlerFrom( final WebSocketHandler handler, final WebResource webResource ) {
		final String url = URL.removeTrailingCharacter( webResource.path() );
		final URLMatcher urlMatcher = URLMatcher.compile( "{protocol}://{host}" + url );
		final WebSocketConnectionCallback callbackHandler = new WebSocketConnectionCallbackHandler(
				handler, urlMatcher ,serializer, unserializer, executorService );
		return Handlers.websocket( callbackHandler );
	}

	@Override
	public void unload() {
		executorService.shutdown();
		try {
			executorService.awaitTermination( 30, TimeUnit.SECONDS );
		} catch (InterruptedException e) {}
		if ( !executorService.isTerminated() )
			executorService.shutdownNow();
	}
}