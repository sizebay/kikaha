package io.skullabs.undertow.routing;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import io.skullabs.undertow.urouting.RoutingMethodClassGenerator;
import io.skullabs.undertow.urouting.RoutingMethodData;

import java.io.StringWriter;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

public class RoutingMethodClassGeneratorTest extends TestCase {

	final StringWriter outputFile = new StringWriter();
	final Filer filer = createAnnotationFiler();

	@Test
	@SneakyThrows
	public void grantThatGeneratesANewClassForNonResponsesAndParameters() {
		val generator = new RoutingMethodClassGenerator( filer );
		RoutingMethodData data = createData( null, "application/json", "" );
		data = fixAnIdentier( data, 123 );
		generator.generate( data );
		String expectedGeneratedClass = readFile( "routing-method-class-generator.expected-non-response-class.txt" );
		assertEquals( expectedGeneratedClass, outputFile.toString() );
	}

	@Test
	@SneakyThrows
	public void grantThatGeneratesANewClassWithResponseAndParametersButNoContentType() {
		val generator = new RoutingMethodClassGenerator( filer );
		RoutingMethodData data = createData( "String", null, "true" );
		data = fixAnIdentier( data, 123 );
		generator.generate( data );
		String expectedGeneratedClass = readFile( "routing-method-class-generator.expected-response-and-param-but-no-content-type-class.txt" );
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

	RoutingMethodData createData( String returnType, String responseContentType, String params ) {
		return new RoutingMethodData(
				SampleRoute.class.getCanonicalName(),
				SampleRoute.class.getPackage().toString(),
				"renderRelatoMais", params, returnType, responseContentType, "/hello/world", "GET" );
	}

	@SneakyThrows
	Filer createAnnotationFiler() {
		JavaFileObject fileObject = mock( JavaFileObject.class );
		when( fileObject.openWriter() ).thenReturn( outputFile );
		Filer filer = mock( Filer.class );
		when( filer.createSourceFile( any( String.class ) ) ).thenReturn( fileObject );
		return filer;
	}

	RoutingMethodData fixAnIdentier( RoutingMethodData data, long time ) {
		final RoutingMethodData stub = spy( data );
		when( stub.getIdentifier() ).thenReturn( time );
		return stub;
	}
}
