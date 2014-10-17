package kikaha.urouting;

import static kikaha.urouting.AnnotationProcessorUtil.extractCanonicalName;
import static kikaha.urouting.AnnotationProcessorUtil.extractMethodParamsFrom;
import static kikaha.urouting.AnnotationProcessorUtil.extractPackageName;
import static kikaha.urouting.AnnotationProcessorUtil.extractServiceInterfaceFrom;
import static kikaha.urouting.AnnotationProcessorUtil.retrieveFirstMethodAnnotatedWith;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import kikaha.urouting.api.OnClose;
import kikaha.urouting.api.OnError;
import kikaha.urouting.api.OnMessage;
import kikaha.urouting.api.OnOpen;
import kikaha.urouting.api.WebSocket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode( exclude = "identifier" )
@RequiredArgsConstructor
public class WebSocketData {

	final String packageName;
	final String canonicalName;
	final String httpPath;
	final String serviceInterface;

	final WebSocketMethodData onOpenMethod;
	final WebSocketMethodData onTextMethod;
	final WebSocketMethodData onCloseMethod;
	final WebSocketMethodData onErrorMethod;

	@Getter( lazy = true )
	private final Long identifier = createIdentifier();

	private Long createIdentifier() {
		return hashCode() & 0xffffffffl;
	}

	public static WebSocketData from( final TypeElement clazz ) {
		return new WebSocketData(
			extractPackageName( clazz ),
			extractCanonicalName( clazz ),
			extractEndpointPathFrom( clazz ),
			extractServiceInterfaceFrom( clazz ),
			retrieveMethodAnnotatedWith( clazz, OnOpen.class ),
			retrieveMethodAnnotatedWith( clazz, OnMessage.class ),
			retrieveMethodAnnotatedWith( clazz, OnClose.class ),
			retrieveMethodAnnotatedWith( clazz, OnError.class ) );
	}

	static String extractEndpointPathFrom( final TypeElement clazz ) {
		final String path = clazz.getAnnotation( WebSocket.class ).value();
		return String.format( "/%s/", path )
			.replaceAll( "//+", "/" );
	}

	static WebSocketMethodData retrieveMethodAnnotatedWith( final TypeElement clazz, final Class<? extends Annotation> annotation ) {
		final ExecutableElement method = retrieveFirstMethodAnnotatedWith( clazz, annotation );
		if ( method == null )
			return null;
		return new WebSocketMethodData(
			method.getSimpleName().toString(),
			extractMethodParamsFrom( method, new WebSocketParameterParser() ) );
	}
}

@RequiredArgsConstructor
class WebSocketMethodData {

	final String name;
	final String parameters;
}