package kikaha.core.test;

import kikaha.config.*;
import kikaha.core.cdi.DefaultCDI;
import org.junit.runner.*;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

/**
 * Implementation based on Mockito's {@code JUnit45AndHigherRunnerImpl}.
 */
public class KikahaRunner extends Runner implements Filterable {

	private final DefaultCDI cdi;
	private final BlockJUnit4ClassRunner runner;

	public KikahaRunner( Class<?> clazz ) throws InitializationError {
		cdi = new DefaultCDI();
		cdi.dependencyFor( Config.class, ConfigLoader.loadDefaults() );
		cdi.loadAllCustomClassConstructors();

		runner = new BlockJUnit4ClassRunner( clazz ) {
			@Override
			protected Statement withBefores( FrameworkMethod method, Object target, Statement statement ) {
				cdi.injectOn( target );
				return super.withBefores( method, target, statement );
			}
		};
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
}