package kikaha.cdi.tests;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import kikaha.core.cdi.*;
import kikaha.core.cdi.DefaultCDI;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

public class PostConstructAndPreDestroyStatelessServiceTest {

	// @Inject
	PostConstructAndPreDestroyStatelessService stateless;

	@Inject
	PostConstructorSingletonService singleton;

	@Before
	public void provideDependencies() throws ServiceProviderException {
		final DefaultCDI provider = new DefaultCDI();
		stateless = provider.load( PostConstructAndPreDestroyStatelessService.class );
		provider.injectOn( this );
	}

	@Test
	public void ensureThatCalledAllCallbacksOnStateless() {
		val status = stateless.getStatus();
		assertEquals( 1, status.calledPostContructJavaAnnotation );
		assertEquals( 1, status.calledPreDestroyJavaAnnotation );
	}

	@Test
	public void ensureThatCalledOnlyPostConstructorCallbacksOnSingleton() {
		val status = singleton.getStatus();
		assertEquals( 1, status.calledPostContructJavaAnnotation );
		assertEquals( 0, status.calledPreDestroyJavaAnnotation );
	}
}
