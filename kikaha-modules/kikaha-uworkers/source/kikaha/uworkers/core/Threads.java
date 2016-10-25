package kikaha.uworkers.core;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class Threads {

	final Queue<Future<?>> asyncJobs = new ArrayDeque<>();
	final ExecutorService executorService;

	public Threads( final ExecutorService executorService ) {
		this.executorService = executorService;
		Runtime.getRuntime().addShutdownHook( new Thread( this::shutdown ) );
	}

	public static Threads elasticPool() {
		return new Threads( Executors.newCachedThreadPool() );
	}

	public static Threads fixedPool( int numberOfThreads ) {
		return new Threads( Executors.newFixedThreadPool( numberOfThreads ) );
	}

	public void submit( Runnable runnable ) {
		final Future<?> future = executorService.submit(runnable);
		asyncJobs.add( future );
	}

	public BackgroundJob background() {
		return new BackgroundJob();
	}

	public void shutdown(){
		log.debug("Shutting down thread pool... " + asyncJobs.size() + " jobs still running.");
		try {
			executorService.shutdown();
			Future<?> future;
			while ((future = asyncJobs.poll()) != null)
				try {
					future.get(100, MILLISECONDS);
				} catch ( TimeoutException c ) {}
			executorService.shutdownNow();
		} catch ( final Exception cause ) {
			throw new RuntimeException(cause);
		}
	}

	public class BackgroundJob implements AutoCloseable {

		final Queue<Future<?>> asyncJobs = new ArrayDeque<>();
		Consumer<Exception> onError = e -> e.printStackTrace();

		public BackgroundJob run(RunnableThatMayFail runnable ) {
			final ParallelJobRunner jobRunner = new ParallelJobRunner(runnable, onError);
			final Future<?> future = executorService.submit(jobRunner);
			asyncJobs.add( future );
			return this;
		}

		public BackgroundJob onError( Consumer<Exception> errorHandler ){
			if ( !asyncJobs.isEmpty() )
				throw new IllegalStateException( "Cannot handle while jobs are running..." );
			this.onError = errorHandler;
			return this;
		}

		public void await() {
			try {
				Future<?> future;
				while ((future = asyncJobs.poll()) != null)
					future.get();
			} catch ( ExecutionException | InterruptedException c ) {
				throw new IllegalStateException(c);
			}
		}

		@Override
		public void close() {
			await();
		}
	}

	@FunctionalInterface
	public interface RunnableThatMayFail {
		void run() throws Exception;
	}
}

@RequiredArgsConstructor
class ParallelJobRunner implements Runnable {

	final Threads.RunnableThatMayFail runnable;
	final Consumer<Exception> onErrorHandler;

	@Override
	public void run() {
		try {
			runnable.run();
		} catch ( Exception e ) {
			onErrorHandler.accept( e );
		}
	}
}