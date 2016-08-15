package kikaha.core.test;

import kikaha.config.*;
import kikaha.core.cdi.DefaultServiceProvider;
import org.junit.runner.*;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

/**
 * Implementation based on Mockito's {@code JUnit45AndHigherRunnerImpl}.
 */
public class KikahaRunner extends Runner implements Filterable {

	private final DefaultServiceProvider cdi = new DefaultServiceProvider();
	private final BlockJUnit4ClassRunner runner;

	public KikahaRunner( Class<?> klass ) throws InitializationError {
		cdi.providerFor( Config.class, ConfigLoader.loadDefaults() );
		cdi.loadAllCustomClassConstructors();

		runner = new BlockJUnit4ClassRunner( klass ) {
			@Override
			protected Statement withBefores( FrameworkMethod method, Object target, Statement statement ) {
				cdi.provideOn( target );
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