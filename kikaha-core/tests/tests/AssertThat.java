package tests;

import static org.junit.Assert.fail;

public abstract class AssertThat {

	public static <T> void isInstance( final T object, final Class<? extends T> clazz ) {
		if ( !clazz.isInstance( object ) )
			fail( object + " is not instance of " + clazz.getCanonicalName() );
	}
}
