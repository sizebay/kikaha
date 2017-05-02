package kikaha.core.cdi.helpers;

import kikaha.core.cdi.DefaultCDI;

public interface ProvidableField {

	void provide( final Object instance, final DefaultCDI.DependencyInjector provider ) throws Throwable;
}