package kikaha.core.cdi;

import static org.junit.Assert.assertEquals;
import lombok.SneakyThrows;

import org.junit.Test;

public class ApplicationRunnerTest {

	@Test
	@SneakyThrows
	public void ensureThatCouldRunTheCountDownApplication() {
		final ApplicationRunner runner = new ApplicationRunner();
		assertEquals( CountDownApplication.class, runner.application.getClass() );

		final CountDownApplication application = (CountDownApplication)runner.application;
		application.setNumber( 1 );
		runner.run();

		assertEquals( 0, application.number );
	}

}
