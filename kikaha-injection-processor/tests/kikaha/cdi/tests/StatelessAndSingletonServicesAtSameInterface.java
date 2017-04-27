package kikaha.cdi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import kikaha.core.cdi.*;
import kikaha.core.cdi.DefaultCDI;
import lombok.val;

import org.junit.Test;

public class StatelessAndSingletonServicesAtSameInterface {

	final DefaultCDI provider = new DefaultCDI();

	@Test
	public void ensureThatCouldFoundBothImplementations() throws ServiceProviderException {
		val services = provider.loadAll( Runnable.class );
		val list = new ArrayList<Runnable>();
		for ( val service : services )
			list.add( service );
		assertThat( list.size(), is( 2 ) );
	}
}
