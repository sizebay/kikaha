package kikaha.cdi.tests.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import kikaha.core.cdi.DefaultServiceProvider;
import kikaha.core.cdi.ServiceProviderException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PrinterRunner implements Runnable {

	final BlockingQueue<Object> events;
	final DefaultServiceProvider provider;
	final CountDownLatch couter;

	@Override
	public void run() {
		try {
			Object last = null;
			while ( last == null || !last.equals("END") ) {
				last = nextEvent();
				instantiateService().printNames();
			}
			// } catch ( final InterruptedException cause ) {
		} catch ( final Throwable cause ) {
			cause.printStackTrace();
		}
	}

	Object nextEvent() throws InterruptedException {
		try {
			return events.take();
		} finally {
			couter.countDown();
		}
	}

	StatelessService instantiateService() {
		try {
			return provider.load( StatelessService.class );
		} catch ( final ServiceProviderException e ) {
			throw new RuntimeException( e );
		}
	}
}
