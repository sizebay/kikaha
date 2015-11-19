package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import kikaha.core.test.KikahaRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import trip.spi.Provided;

@RunWith( KikahaRunner.class )
public class KikahaRunnerTest {

	@Provided
	HelloService helloService;

	@Test
	public void ensureThatCanRetrieveWhoShouldISayHelloTo()
	{
		assertNotNull( helloService );
		final String name = helloService.getWhoShouldISayHelloTo();
		assertEquals( "World", name );
	}
}
