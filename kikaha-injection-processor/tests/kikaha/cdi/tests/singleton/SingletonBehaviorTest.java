package kikaha.cdi.tests.singleton;

import static org.junit.Assert.assertSame;

import javax.inject.Inject;

import kikaha.core.cdi.DefaultServiceProvider;

import org.junit.Before;
import org.junit.Test;

public class SingletonBehaviorTest {

	@Inject
	Closeable closeable;

	@Inject
	Reader reader;

	@Inject
	HelloWorldReader helloReader;

	@Before
	public void provideDependencies(){
		new DefaultServiceProvider().provideOn(this);
	}

	@Test
	public void ensureThatBothInjectedDataIsSame(){
		assertSame(closeable, helloReader);
		assertSame(reader, helloReader);
	}
}
