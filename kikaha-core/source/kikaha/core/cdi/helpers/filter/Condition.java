package kikaha.core.cdi.helpers.filter;

public interface Condition<T> {
	
	boolean check( T object );
}