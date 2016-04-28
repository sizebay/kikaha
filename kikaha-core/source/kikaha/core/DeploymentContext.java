package kikaha.core;

import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;
import kikaha.core.url.SimpleRoutingHandler;
import kikaha.core.url.URL;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Accessors( fluent = true )
@Singleton
public class DeploymentContext {

	final Map<String, Object> attributes = new HashMap<>();
	final SimpleRoutingHandler routingHandler = new SimpleRoutingHandler();

	@NonNull
	@Setter
	HttpHandler rootHandler = routingHandler;

	@Inject
	NotFoundHandler notFoundHandler;

	@PostConstruct
	public void ensureThatUseNotFoundHandlerAsDefaultFallbackHandler(){
		routingHandler.setFallbackHandler( notFoundHandler );
	}

	public DeploymentContext register(final String uri, final HttpHandler handler ) {
		register( uri, "PATCH", handler );
		register( uri, Methods.PUT_STRING, handler );
		register( uri, Methods.POST_STRING, handler );
		register( uri, Methods.DELETE_STRING, handler );
		return register( uri, Methods.GET_STRING, handler );
	}

	public DeploymentContext register(String uri, final String method, final HttpHandler handler ) {
		uri = URL.removeTrailingCharacter( uri );
		log.info( "Registering route: " + method + ":" + uri );
		this.routingHandler.add( method, uri, handler );
		return this;
	}

	public DeploymentContext fallbackHandler(final HttpHandler fallbackHandler ) {
		routingHandler.setFallbackHandler( fallbackHandler );
		return this;
	}

	public HttpHandler fallbackHandler(){
		return routingHandler.getFallbackHandler();
	}
}