package kikaha.core.cdi.helpers;

import java.util.*;

public abstract class ServiceLoader {

	public static <T> List<Class<T>> loadImplementationsFor( Class<T> clazz ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( clazz, cl );
		return readAndConvertToList( reader );
	}

	public static <T> List<Class<T>> loadImplementationsFor( String clazz ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( clazz, cl );
		return readAndConvertToList( reader );
	}

	private static <T> List<Class<T>> readAndConvertToList( final Iterator<Class<T>> reader ) {
		final List<Class<T>> list = new TinyList<>();
		while ( reader.hasNext() )
			try {
                Class<T> c = reader.next();
                if ( !list.contains(c) )
				    list.add( c );
			} catch ( IllegalStateException cause ) {
				continue;
			}
		return list;
	}
}
