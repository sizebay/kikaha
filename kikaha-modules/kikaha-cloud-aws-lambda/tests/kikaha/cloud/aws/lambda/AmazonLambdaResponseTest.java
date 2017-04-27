package kikaha.cloud.aws.lambda;

import static org.junit.Assert.*;

import java.util.*;
import kikaha.urouting.api.*;
import org.junit.Test;

/**
 * Unit tests for AmazonLambdaResponse.
 */
public class AmazonLambdaResponseTest {

	final Map<String, String> user = Collections.singletonMap("name", "Helden");
	final String userAsJson = "{\"name\":\"Helden\"}";

	@Test
	public void ensureCanCreateAResponseFromObject() throws Exception {
		final AmazonLambdaResponse response = AmazonLambdaResponse.with( user );
		assertEquals( userAsJson, response.body );
		assertEquals( 200, response.statusCode );
		assertEquals( Mimes.JSON, response.headers.get( "Content-Type" ) );
	}

	@Test
	public void ensureCanCreateAResponseFromURoutingResponse() throws Exception {
		final DefaultResponse defaultResponse = DefaultResponse.ok( user ).statusCode( 201 );
		final AmazonLambdaResponse response = AmazonLambdaResponse.with( defaultResponse );
		assertEquals( userAsJson, response.body );
		assertEquals( 201, response.statusCode );
		assertFalse( response.headers.isEmpty() );
		assertEquals( Mimes.JSON, response.headers.get( "Content-Type" ) );
	}

	@Test
	public void ensureNoContent() throws Exception {
		final AmazonLambdaResponse response = AmazonLambdaResponse.noContent();
		assertNull( response.body );
		assertEquals( 204, response.statusCode );
		assertTrue( response.headers.isEmpty() );
	}

}