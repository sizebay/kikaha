package kikaha.urouting;

import static java.lang.String.format;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.function.BiFunction;
import io.undertow.websockets.core.CloseMessage;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.EventDispatcher.Matcher;
import kikaha.urouting.api.*;
import lombok.*;

class WebSocketParameterParser implements BiFunction<ExecutableElement, VariableElement, String> {

	final EventDispatcher<VariableElement> dispatcher = createEventDispatcher();
	StringBuilder parsedData;

	EventDispatcher<VariableElement> createEventDispatcher() {
		return new EventDispatcher<VariableElement>()
			.when( new ContainsAnnotation( PathParam.class ), this::handlePathParam )
			.when( new ContainsAnnotation( HeaderParam.class ), this::handleHeaderParam )
			.when( ParamType.is( String.class ), this::handleStringBody )
			.when( ParamType.is( CloseMessage.class ), this::handleCloseMessageBody )
			.when( ParamType.is( Throwable.class ), this::handleThrowableBody )
			.when( ParamType.is( WebSocketSession.class ), this::handleWebSocketSessionBody )
			.when( UnknownParam.any(), this::trySerializeAnyOtherBodyContent );
	}

	void handlePathParam( final VariableElement parameter ) {
		final String param = parameter.getAnnotation( PathParam.class ).value();
		final String targetType = parameter.asType().toString();
		parsedData.append( format(
			"dataProvider.getPathParam( session, \"%s\", %s.class )",
			param, targetType ) );
	}

	void handleHeaderParam( final VariableElement parameter ) {
		final String param = parameter.getAnnotation( HeaderParam.class ).value();
		final String targetType = parameter.asType().toString();
		parsedData.append( format(
			"dataProvider.getHeaderParam( session, \"%s\", %s.class )",
			param, targetType ) );
	}

	void handleStringBody( final VariableElement parameter ) {
		parsedData.append( "message" );
	}

	void handleCloseMessageBody( final VariableElement parameter ) {
		parsedData.append( "cm" );
	}

	void handleThrowableBody( final VariableElement parameter ) {
		parsedData.append( "cause" );
	}

	void handleWebSocketSessionBody( final VariableElement parameter ) {
		parsedData.append( "session" );
	}

	void trySerializeAnyOtherBodyContent( final VariableElement parameter ) {
		final String typeAsString = parameter.asType().toString();
		parsedData.append( "dataProvider.getBody( session, message, " + typeAsString + ".class )" );
	}

	/**
	 * Extract method parameter for a given {@link VariableElement} argument.
	 * The returned method parameter will be passed as argument to a routing
	 * method.
	 *
	 * @param method
	 * @param parameter
	 * @return
	 */
	@Override
	public String apply( final ExecutableElement method, final VariableElement parameter ) {
		parsedData = new StringBuilder();
		dispatcher.apply( parameter );
		return parsedData.toString();
	}
}

@RequiredArgsConstructor
class ContainsAnnotation implements Matcher<VariableElement> {

	final Class<? extends Annotation> annotation;

	@Override
	public boolean matches( final VariableElement object ) {
		return object.getAnnotation( annotation ) != null;
	}
}

@RequiredArgsConstructor( staticName = "is" )
class ParamType implements Matcher<VariableElement> {

	final Class<?> clazz;

	@Override
	public boolean matches( final VariableElement object ) {
		return object.asType().toString().equals( clazz.getCanonicalName() );
	}
}

@NoArgsConstructor(staticName= "any" )
class UnknownParam implements Matcher<VariableElement> {

	@Override
	public boolean matches( final VariableElement object ) {
		return true;
	}
}