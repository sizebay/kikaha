package kikaha.core.test;

import kikaha.core.KikahaUndertowServer;
import kikaha.core.cdi.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.*;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

/**
 * Implementation based on Mockito's {@code JUnit45AndHigherRunnerImpl}.
 */
@Slf4j
public class KikahaServerRunner extends Runner implements Filterable {

	private static final KikahaUndertowServer server = new KikahaUndertowServer();
	private static final CDI cdi = DefaultCDI.newInstance();
	private final BlockJUnit4ClassRunner runner;

	static {
		cdi.injectOn( server );
		try {
			server.run();
			log.info( "Unit test server started." );
		} catch ( Exception e ) {
			throw new IllegalStateException( e );
		}
	}

	public KikahaServerRunner( Class<?> clazz ) throws InitializationError {
		try {
			runner = new KikahaUnitTestRunner( clazz );
		} catch ( Exception e ) {
			throw new InitializationError( e );
		}
	}

	@Override
	public void run( final RunNotifier notifier ) {
		runner.run( notifier );
	}

	@Override
	public Description getDescription() {
		return runner.getDescription();
	}

	@Override
	public void filter( Filter filter ) throws NoTestsRemainException {
		runner.filter( filter );
	}

	class KikahaUnitTestRunner extends BlockJUnit4ClassRunner {

		/**
		 * Creates a BlockJUnit4ClassRunner to run {@code clazz}
		 *
		 * @param clazz
		 * @throws InitializationError if the test class is malformed.
		 */
		public KikahaUnitTestRunner( Class<?> clazz ) throws InitializationError {
			super( clazz );
		}

		@Override
		protected Statement withBefores( FrameworkMethod method, Object target, Statement statement ) {
			cdi.injectOn( target );
			return super.withBefores( method, target, statement );
		}
	}
}