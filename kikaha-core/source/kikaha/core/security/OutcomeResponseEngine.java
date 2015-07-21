package kikaha.core.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

abstract class OutcomeResponseEngine {

	public static <I,T> OutcomeResponse<T> run( Iterable<Checkable<I,T>> checkables, I input ){
		OutcomeResponse<T> last = null;
		for ( final Checkable<I,T> checkable : checkables ){
			final OutcomeResponse<T> current = checkable.check(input);
			if ( current.isMoreRelevantThan(last) )
				last = current;
		}
		return last;
	}

	@RequiredArgsConstructor
	public enum Outcome {
		NOT_ATTEMPTED(0), FAILED(1), SUCCESS(2);
		final int relevance;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OutcomeResponse<T> {
		final T response;
		final Outcome outcome;

		boolean isMoreRelevantThan( OutcomeResponse<T> last ){
			return last == null || last.outcome.relevance < outcome.relevance;
		}
	}

	public interface Checkable<I,O> {
		OutcomeResponse<O> check( I input );
	}
}
