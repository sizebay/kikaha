package kikaha.urouting;

import io.undertow.websockets.core.CloseMessage;
import kikaha.core.modules.websocket.WebSocketSession;
import kikaha.urouting.api.HeaderParam;
import kikaha.urouting.api.PathParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith( MockitoJUnitRunner.class )
public class WebSocketParameterParserTest {

	final WebSocketParameterParser parser = new WebSocketParameterParser();

	@Mock
	ExecutableElement method;

	@Mock
	VariableElement parameter;

	@Mock
	TypeMirror parameterType;

	@Mock
	PathParam pathAnnotation;

	@Mock
	HeaderParam headerAnnotation;

	@Test
	public void ensureThatCouldProvideAPathParameter() {
		defineMethodParameterAs( Long.class );
		doReturn( pathAnnotation ).when( parameter ).getAnnotation( PathParam.class );
		doReturn( "id" ).when( pathAnnotation ).value();
		final String parsed = parser.parse( method, parameter );
		assertEquals( "dataProvider.getPathParam( session, \"id\", java.lang.Long.class )", parsed );
	}

	@Test
	public void ensureThatCouldProvideAHeaderParameter() {
		defineMethodParameterAs( Long.class );
		doReturn( headerAnnotation ).when( parameter ).getAnnotation( HeaderParam.class );
		doReturn( "id" ).when( headerAnnotation ).value();
		final String parsed = parser.parse( method, parameter );
		assertEquals( "dataProvider.getHeaderParam( session, \"id\", java.lang.Long.class )", parsed );
	}

	@Test
	public void ensureThatCouldProvideAMessage() {
		defineMethodParameterAs( String.class );
		doReturn( null ).when( parameter ).getAnnotation( PathParam.class );
		final String parsed = parser.parse( method, parameter );
		assertEquals( "message", parsed );
	}

	@Test
	public void ensureThatCouldProvideACloseMessage() {
		defineMethodParameterAs( CloseMessage.class );
		doReturn( null ).when( parameter ).getAnnotation( PathParam.class );
		final String parsed = parser.parse( method, parameter );
		assertEquals( "cm", parsed );
	}

	@Test
	public void ensureThatCouldProvideAWebSocketSession() {
		defineMethodParameterAs( WebSocketSession.class );
		doReturn( null ).when( parameter ).getAnnotation( PathParam.class );
		final String parsed = parser.parse( method, parameter );
		assertEquals( "session", parsed );
	}

	@Test
	public void ensureThatCouldProvideAThrowable() {
		defineMethodParameterAs( Throwable.class );
		doReturn( null ).when( parameter ).getAnnotation( PathParam.class );
		final String parsed = parser.parse( method, parameter );
		assertEquals( "cause", parsed );
	}

	private void defineMethodParameterAs( final Class<?> clazz ) {
		doReturn( parameterType ).when( parameter ).asType();
		doReturn( clazz.getCanonicalName() ).when( parameterType ).toString();
	}
}