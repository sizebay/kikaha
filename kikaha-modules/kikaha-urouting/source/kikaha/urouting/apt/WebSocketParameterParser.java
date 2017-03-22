package kikaha.urouting.apt;

import static java.lang.String.format;
import static kikaha.apt.APT.asType;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import io.undertow.websockets.core.CloseMessage;
import kikaha.apt.*;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.*;

public class WebSocketParameterParser extends MethodParametersExtractor {

	public WebSocketParameterParser() {
		super( createWebSocketAnnotationRules(), WebSocketParameterParser::trySerializeAnyOtherBodyContent );
	}

	static ChainedRules<VariableElement, Function<VariableElement, String>> createWebSocketAnnotationRules(){
		final ChainedRules<VariableElement, Function<VariableElement, String>> rules = new ChainedRules<>();
		rules
		   .with( isAnnotatedWith( PathParam.class ), v -> getParam( PathParam.class, v.getAnnotation( PathParam.class ).value(), v ) )
			.and( isAnnotatedWith( HeaderParam.class ), v -> getParam( HeaderParam.class, v.getAnnotation( HeaderParam.class ).value(), v ) )
			.and( whichTypeIs( String.class ), v -> "message" )
			.and( whichTypeIs( CloseMessage.class ), v -> "cm" )
			.and( whichTypeIs( WebSocketSession.class ), v -> "session" )
			.and( whichTypeIs( Throwable.class ), v -> "cause" );
		return rules;
	}

	static Function<VariableElement, Boolean> isAnnotatedWith( Class<? extends Annotation> annotationClass ) {
		return v -> APT.isAnnotatedWith( v, annotationClass );
	}

	static Function<VariableElement, Boolean> whichTypeIs( Class<?> clazz ) {
		return v -> v.asType().toString().equals( clazz.getCanonicalName() );
	}

	static String getParam( final Class<?> targetAnnotation, final String param, final VariableElement parameter ) {
		final String targetType = asType( parameter );
		return format( "dataProvider.get%s( session, \"%s\", %s.class )",
				targetAnnotation.getSimpleName(), param, targetType );
	}

	@SuppressWarnings( "unused" )
	static String trySerializeAnyOtherBodyContent( final ExecutableElement executableElement, final VariableElement parameter ) {
		final String typeAsString = parameter.asType().toString();
		return "dataProvider.getBody( session, message, " + typeAsString + ".class )";
	}
}