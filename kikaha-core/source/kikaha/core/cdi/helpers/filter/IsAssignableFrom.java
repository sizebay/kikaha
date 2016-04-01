package kikaha.core.cdi.helpers.filter;

import kikaha.core.cdi.ProducerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IsAssignableFrom implements Condition<Object> {

	final Class<?> expectedClass;

	@Override
	public boolean check( Object object ) {
		return object == null
			|| expectedClass.isAssignableFrom( object.getClass() )
			|| ProducerFactory.class.isInstance( object );
	}

	@Override
	public String toString() {
		return "IsAssignableFrom(" + expectedClass.getCanonicalName() + ")";
	}
}
