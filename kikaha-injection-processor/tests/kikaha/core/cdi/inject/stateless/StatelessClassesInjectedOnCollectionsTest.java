package kikaha.core.cdi.inject.stateless;

import java.util.Collection;
import java.util.Iterator;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import kikaha.core.cdi.DefaultCDI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StatelessClassesInjectedOnCollectionsTest {
	
	@Inject
	@Typed( SuperInterface.class )
	Collection<SuperInterface> superInterfaces;

	@Before
	public void injectDeps(){
		DefaultCDI.newInstance().injectOn(this);
	}

	@Test
	public void ensureCanInjectStatelessServicesOnCollections(){
		Iterator<SuperInterface> iteratorOfSuperInterfaces = superInterfaces.iterator();
		Assert.assertTrue( iteratorOfSuperInterfaces.hasNext() );
		Assert.assertTrue( StatelessSuperClass.class.isInstance( iteratorOfSuperInterfaces.next() ) );
	}
}
