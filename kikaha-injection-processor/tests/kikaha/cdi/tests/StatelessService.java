package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;

import kikaha.core.cdi.Stateless;

@Stateless
@Typed( Runnable.class )
public class StatelessService implements Runnable {

	@Override
	public void run() {}
}
