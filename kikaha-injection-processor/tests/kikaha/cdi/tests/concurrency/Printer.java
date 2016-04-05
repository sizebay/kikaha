package kikaha.cdi.tests.concurrency;

import javax.inject.Singleton;

@Singleton
public class Printer {

	public void print( final String msg ) {
		//System.out.println( msg );
	}
}
