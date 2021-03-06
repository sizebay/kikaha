package kikaha.urouting;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import kikaha.urouting.samples.RetrieveRoutes;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Test;

public class RoutingMethodClassGeneratorTest {

	final StringWriter outputFile = new StringWriter();
	final Filer filer = createAnnotationFiler();

	@Test
	@SneakyThrows
	public void grantThatGeneratesANewClassForNonResponsesAndParameters() {
		val generator = new RoutingMethodClassGenerator( filer );
		RoutingMethodData data = createData( null, "application/json", "", "java.lang.String" );
		data = fixAnIdentier( data, 123 );
		generator.generate( data );
		final String expectedGeneratedClass = TestCase.readFile( "routing-method-class-generator.expected-non-response-class.txt" );
		assertEquals( expectedGeneratedClass, outputFile.toString() );
	}

	@Test
	@SneakyThrows
	public void grantThatGeneratesANewClassWithResponseAndParametersButNoContentType() {
		val generator = new RoutingMethodClassGenerator( filer );
		RoutingMethodData data = createData( "String", null, "true" );
		data = fixAnIdentier( data, 123 );
		generator.generate( data );
		final String expectedGeneratedClass = TestCase
				.readFile( "routing-method-class-generator.expected-response-and-param-but-no-content-type-class.txt" );
		assertEquals( expectedGeneratedClass, outputFile.toString() );
	}

	@Test
	@SneakyThrows
	public void grantThatDoesntGenerateTwoEqualClassesEvenWithDataWithSameParameters() {
		val generator = new RoutingMethodClassGenerator( filer );
		RoutingMethodData data = createData( null, "application/json", "" );
		generator.generate( data );
		val firstGeneratedClass = outputFile.toString();
		data = createData( null, "application/json", "" );
		generator.generate( data );
		assertThat( firstGeneratedClass, not( outputFile.toString() ) );
	}

	RoutingMethodData createData( final String returnType, final String responseContentType, final String params ) {
		return createData( returnType, responseContentType, params, null );
	}

	RoutingMethodData createData( final String returnType, final String responseContentType, final String params, final String serviceInterface ) {
		final boolean hasIO = isNotEmpty(returnType) || isNotEmpty( params );
		return new RoutingMethodData(
			RetrieveRoutes.class.getCanonicalName(),
			RetrieveRoutes.class.getPackage().toString(),
			"renderRelatoMais", params, returnType, responseContentType,
			"/hello/world", "GET", serviceInterface, hasIO, false, false );
	}

	boolean isNotEmpty( String s ){
		return s != null && !s.isEmpty();
	}

	@SneakyThrows
	Filer createAnnotationFiler() {
		final JavaFileObject fileObject = mock( JavaFileObject.class );
		when( fileObject.openWriter() ).thenReturn( outputFile );
		final Filer filer = mock( Filer.class );
		when( filer.createSourceFile( any( String.class ) ) ).thenReturn( fileObject );
		return filer;
	}

	RoutingMethodData fixAnIdentier( final RoutingMethodData data, final long time ) {
		final RoutingMethodData stub = spy( data );
		when( stub.getIdentifier() ).thenReturn( time );
		return stub;
	}
}
