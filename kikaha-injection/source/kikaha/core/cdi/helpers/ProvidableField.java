package kikaha.core.cdi.helpers;

import kikaha.core.cdi.DefaultServiceProvider;

public interface ProvidableField {

	public void provide( final Object instance, final DefaultServiceProvider.DependencyInjector provider ) throws Throwable;
}