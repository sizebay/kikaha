package kikaha.core.cdi.helpers;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * Created by miere.teixeira on 03/11/2017.
 */
public interface Reference<T> {

    static <T> Reference<T> optional() {
        return new DefaultReference<T>();
    }

    static <T> Reference<T> mandatory( String msg, T initialValue ) {
        return new MandatoryReference<T>( msg ).set( initialValue );
    }

    default T getOrElse(Supplier<T> supplier) {
        T value = get();
        if ( value == null )
            value = supplier.get();
        return value;
    }

    T get();

    Reference<T> set( T newValue );

    class DefaultReference<T> implements Reference<T> {

        private T value;

        @Override
        public T get() {
            return value;
        }

        @Override
        public DefaultReference<T> set(T newValue) {
            this.value = newValue;
            return this;
        }
    }

    @RequiredArgsConstructor
    class MandatoryReference<T> extends DefaultReference<T> {

        final String msg;

        @Override
        public T getOrElse(Supplier<T> supplier) {
            T value = get();
            if ( value == null )
                value = supplier.get();
            if ( value == null )
                throw new IllegalArgumentException( msg );
            return value;
        }
    }
}
