package kikaha.core.util;

import java.util.*;
import java.util.function.Function;

/**
 *
 */
public interface Lang {

	static <O,N> List<N> convert(Iterable<O>originalItems, Function<O, N> converter) {
		final List<N> newItems = new ArrayList<>();
		for ( O original : originalItems )
			newItems.add( converter.apply( original ) );
		return newItems;
	}

}
