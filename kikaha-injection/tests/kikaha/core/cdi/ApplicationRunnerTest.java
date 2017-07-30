package kikaha.core.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import lombok.*;
import org.junit.*;

public class ApplicationRunnerTest {

	@Before
	public void setup(){
		System.clearProperty( "application-class" );
	}

	@Test
	@SneakyThrows
	public void ensureThatCouldRunTheCountDownApplication() {
		final ApplicationRunner runner = new ApplicationRunner();
		assertEquals( CountDownApplication.class, runner.loadApplication().getClass() );

		final CountDownApplication application = (CountDownApplication)runner.loadApplication();
		application.setNumber( 1 );
		runner.run();

		assertEquals( 0, application.number );
	}

	@Test
	@SneakyThrows
	public void ensureCanRunApplicationDefinedOnSystemProperty(){
		System.setProperty( "application-class", CustomApplication.class.getCanonicalName() );
		final ApplicationRunner runner = new ApplicationRunner();
		runner.run();

		assertTrue( "Did not run the CustomApplication defined on the system property",
			CustomApplication.executed );
	}
}

class CustomApplication implements Application {

	static boolean executed = false;

	@Override
	public void run() throws Exception {
		executed = true;
	}
}