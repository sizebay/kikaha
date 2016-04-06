package tests;

import kikaha.core.test.KikahaRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith( KikahaRunner.class )
public class KikahaRunnerTest {

	@Inject
	HelloService helloService;

	@Test
	public void ensureThatCanRetrieveWhoShouldISayHelloTo()
	{
		assertNotNull( helloService );
		final String name = helloService.getWhoShouldISayHelloTo();
		assertEquals( "World", name );
	}
}
