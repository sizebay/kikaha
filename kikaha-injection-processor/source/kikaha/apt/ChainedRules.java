package kikaha.apt;

import java.util.*;
import java.util.function.Function;
import kikaha.core.cdi.helpers.TinyList;
import lombok.RequiredArgsConstructor;

/**
 *
 */
public class ChainedRules<K,R> implements Iterable<ChainedRules<K,R>.Rule> {

	final List<Rule> ruleList = new TinyList<>();

	public ChainedRules<K,R> with( Function<K, Boolean> matcher, R result ) {
		ruleList.add( new Rule( matcher, result ) );
		return this;
	}

	public ChainedRules<K,R> and( Function<K, Boolean> matcher, R result ) {
		return with( matcher, result );
	}

	@Override
	public Iterator<ChainedRules<K,R>.Rule> iterator() {
		return ruleList.iterator();
	}

	@RequiredArgsConstructor
	public class Rule{
		final Function<K,Boolean> matcher;
		final R result;

		public boolean matches( K key ) {
			return matcher.apply( key );
		}
	}
}
