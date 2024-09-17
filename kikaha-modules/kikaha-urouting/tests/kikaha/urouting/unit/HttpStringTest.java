package kikaha.urouting.unit;

import static org.junit.Assert.*;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import org.junit.Test;

public class HttpStringTest {

	@Test
	public void ensureThatCanCompareACreatedHStringWithAStaticPredefinedOne(){
		assertEquals( Headers.CONTENT_TYPE, new HttpString("Content-Type") );
	}
}
