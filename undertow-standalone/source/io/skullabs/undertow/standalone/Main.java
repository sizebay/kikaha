package io.skullabs.undertow.standalone;

import io.skullabs.undertow.standalone.api.Configuration;
import io.skullabs.undertow.standalone.api.UndertowStandaloneException;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class Main {

	private final Lock lock = new ReentrantLock();
	private final Configuration configuration;
	private UndertowServer undertowServer;

	public void start() throws UndertowStandaloneException {
		lock.lock();
		undertowServer = new UndertowServer( configuration );
		undertowServer.start();
	}

	public void stop(){
		undertowServer.stop();
		lock.unlock();
	}

	public void mainloop() throws InterruptedException, UndertowStandaloneException {
		Condition newCondition = lock.newCondition();
		start();

		try {
			while( true )
				newCondition.awaitNanos(1);
		} catch ( InterruptedException cause ) {
			stop();
		}
	}

	public static void main(String[] args) throws InterruptedException, UndertowStandaloneException, IOException, ClassNotFoundException {
		final Configuration config = args.length == 0 || isBlank( args[0] )
				? DefaultConfiguration.loadDefaultConfiguration()
				: DefaultConfiguration.loadConfiguration( args[0] );
		val main = new Main( config );
		main.mainloop();
	}
	
	static boolean isBlank( String string ) {
		return string == null || string.isEmpty();
	}
}
