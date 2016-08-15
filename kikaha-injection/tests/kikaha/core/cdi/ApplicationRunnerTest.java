package kikaha.core.cdi;

import static org.junit.Assert.assertEquals;
import lombok.SneakyThrows;
import org.junit.Test;

public class ApplicationRunnerTest {

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
}
