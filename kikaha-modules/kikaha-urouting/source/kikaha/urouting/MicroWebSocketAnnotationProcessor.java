package kikaha.urouting;

import static kikaha.apt.APT.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

import kikaha.apt.*;
import kikaha.urouting.api.*;

@SupportedAnnotationTypes( "kikaha.urouting.api.*" )
public class MicroWebSocketAnnotationProcessor extends AnnotationProcessor {

	ClassGenerator generator;

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new ClassGenerator( processingEnv.getFiler(), "websocket-class.mustache" );
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		try {
			final Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith( WebSocket.class );
			for ( final Element method : elementsAnnotatedWith ) {
				debug( "  > " + method );
				generator.generate( toWebSocketData( (TypeElement) method ) );
			}
			return false;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	static WebSocketData toWebSocketData( final TypeElement clazz ) {
		return new WebSocketData(
				extractPackageName( clazz ),
				extractTypeName( asType( clazz ) ),
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
			extractMethodParamsFrom( method, new WebSocketParameterParser() ),
			extractReturnTypeFrom( method ) );
	}
}
