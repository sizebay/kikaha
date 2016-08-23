package kikaha.core.modules.smart;

import static kikaha.core.modules.smart.ReverseProxyClientProvider.createTargetURIFrom;
import static org.junit.Assert.assertEquals;
import java.net.URI;
import org.junit.Test;

/**
 *
 */
public class ReverseProxyUnitTest {

	@Test
	public void ensureThatCanCreateANewURIPointingToRootURL(){
		final URI uri = URI.create("http://localhost:9001/index.html");
		assertEquals( "http://localhost:9001/", createTargetURIFrom(uri).toString() );
	}
}
