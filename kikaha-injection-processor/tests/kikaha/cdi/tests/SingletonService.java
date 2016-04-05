package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed( Runnable.class )
public class SingletonService implements Runnable {

	@Override
	public void run() {}
}
