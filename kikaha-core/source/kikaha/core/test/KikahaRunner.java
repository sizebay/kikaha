package kikaha.core.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Implementation based on Mockito's {@code JUnit45AndHigherRunnerImpl}.
 */
public class KikahaRunner extends Runner implements Filterable {

	private final BlockJUnit4ClassRunner runner;

	public KikahaRunner( Class<?> klass ) throws InitializationError {
		runner = new BlockJUnit4ClassRunner( klass ) {
			@Override
			protected Statement withBefores( FrameworkMethod method, Object target, Statement statement ) {
				KikahaTestCase.injectInto( target );
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