package kikaha.uworkers.core;

import static org.junit.Assert.assertEquals;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Threads}.
 */
public class ThreadsTest {

	static final int TEN = 10;
	final CountDownLatch counter = new CountDownLatch( TEN );

	@Test
	public void canRunManyTasksAndWaitForItsCompletion(){
		try ( final Threads threads = Threads.fixedPool(TEN) ) {
			final Threads.BackgroundJob background = threads.background();
			for (int i = 0; i < TEN; i++)
				background.run(() -> counter.countDown());
			background.await();

			assertEquals(0, counter.getCount());
		}
	}

	@Test
	public void canRunManyTasksAndWaitForItsCompletion_usingTryWithResources(){
		try ( final Threads threads = Threads.fixedPool(TEN) ) {
			try (final Threads.BackgroundJob background = threads.background()) {
				for (int i = 0; i < TEN; i++)
					background.run(() -> counter.countDown());
			}

			assertEquals(0, counter.getCount());
		}
	}

	@Test
	public void canDefineAErrorHandler(){
		try ( final Threads threads = Threads.fixedPool(TEN) ) {
			final Threads.BackgroundJob background = threads.background().onError(e -> counter.countDown());
			for (int i = 0; i < TEN; i++)
				background.run(() -> {
					throw new RuntimeException("");
				});
			background.await();
			assertEquals(0, counter.getCount());
		}
	}

	@Test( expected = IllegalStateException.class )
	public void cannotDefineAErrorBeforeRunJobs(){
		try ( final Threads threads = Threads.fixedPool(TEN) ) {
			final Threads.BackgroundJob background = threads.background();
			for (int i = 0; i < TEN; i++)
				background.run(() -> counter.countDown());
			background.onError(e -> e.printStackTrace());
		}
	}
}
