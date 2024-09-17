package kikaha.uworkers.local;

import kikaha.uworkers.api.Exchange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("unchecked")
public class LocalExchange implements Exchange {

	final Lock lock = new ReentrantLock();

	@Getter
	@Accessors(fluent = true)
	final Object request;

	private volatile BiConsumer<UndefinedObject, Throwable> listener;
	private volatile Object response;
	private volatile Throwable exception;

	@Override
	public LocalExchange then( BiConsumer<UndefinedObject, Throwable> listener ) {
		lock.lock();
		try {
			if ( this.listener != null )
				throw new IllegalStateException( "Listener already set." );
			this.listener = listener;
			tryDispatchResponseToTheListener();
			return this;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <RESP> LocalExchange reply(RESP response) {
		lock.lock();
		try {
			this.response = response;
			tryDispatchResponseToTheListener();
			return this;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public LocalExchange reply(Throwable error) {
		lock.lock();
		try {
			this.exception = error;
			tryDispatchResponseToTheListener();
			return this;
		} finally {
			lock.unlock();
		}
	}

	private void tryDispatchResponseToTheListener() {
		if ( (response != null || exception != null) && listener != null ) {
			listener.accept( new UndefinedObject(response), exception );
		}
	}

	@Override
	public <REQ> REQ requestAs( Class<REQ> requestType ) {
		return (REQ)request();
	}

	@Override
	public <RESP> RESP responseAs( Class<RESP> responseType ) {
		return response();
	}

	@Override
	public <RESP> RESP response() {
		while ( response == null && exception == null )
			LockSupport.parkNanos(1l);

		if ( exception != null )
			throw new IllegalStateException( exception );

		return (RESP)response;
	}

	@Override
	public String toString() {
		return super.toString() + "; Response: " + response + "; Exception: " + exception;
	}

	public static LocalExchange create(){
		return LocalExchange.of( null );
	}
}