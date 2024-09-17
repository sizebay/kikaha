package kikaha.core.cdi.recursive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.inject.Inject;
import kikaha.core.cdi.DefaultCDI;
import org.junit.*;

/**
 *
 */
public class InjectableClassConstructorTest {

	@Inject UserService userService;
	@Inject ClassWithInjectableConstructor clazz;

	@Before
	public void injectDependencies(){
		DefaultCDI.newInstance()
			.injectOn( this );
	}

	@Test
	public void ensureCanInjectClassWithInjectableConstructor(){
		assertNotNull( clazz );
	}

	@Test
	public void ensureWasAbleToInjectASingletonThroughTheClassConstructor(){
		assertNotNull( userService );
		assertEquals( userService, clazz.userService );
	}
}
