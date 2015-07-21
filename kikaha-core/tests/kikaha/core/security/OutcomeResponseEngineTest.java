package kikaha.core.security;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import kikaha.core.security.OutcomeResponseEngine.Checkable;
import kikaha.core.security.OutcomeResponseEngine.Outcome;
import kikaha.core.security.OutcomeResponseEngine.OutcomeResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OutcomeResponseEngineTest {

	final NotAttempted notAttempted = new NotAttempted();
	final Failed failed = new Failed();
	final Success success = new Success();

	@Test
	public void ensureThatReturnsNotAttemptedWhenNoMoreRelevantDataWasFound(){
		final OutcomeResponse<Integer> response = OutcomeResponseEngine.run( Arrays.asList( notAttempted ), 0 );
		assertEquals( NotAttempted.RESULT, response.getResponse(), 0 );
	}

	@Test
	public void ensureThatReturnsFailededWhenNoMoreRelevantDataWasFound(){
		final List<Checkable<Integer, Integer>> checkables = Arrays.asList( failed, notAttempted );
		final OutcomeResponse<Integer> response = OutcomeResponseEngine.run( checkables, 0 );
		assertEquals( Failed.RESULT, response.getResponse(), 0 );
	}

	@Test
	public void ensureThatReturnsSuccessWhenNoMoreRelevantDataWasFound(){
		final List<Checkable<Integer, Integer>> checkables = Arrays.asList( failed, notAttempted, success );
		final OutcomeResponse<Integer> response = OutcomeResponseEngine.run( checkables, 0 );
		assertEquals( Success.RESULT, response.getResponse(), 0 );
	}

}

class NotAttempted implements Checkable<Integer, Integer> {

	static final int RESULT = -1;

	@Override
	public OutcomeResponse<Integer> check(Integer input) {
		return new OutcomeResponse<>(RESULT, Outcome.NOT_ATTEMPTED);
	}
}

class Failed implements Checkable<Integer, Integer> {

	static final int RESULT = 0;

	@Override
	public OutcomeResponse<Integer> check(Integer input) {
		return new OutcomeResponse<>(RESULT, Outcome.FAILED);
	}
}

class Success implements Checkable<Integer, Integer> {

	static final int RESULT = 1;

	@Override
	public OutcomeResponse<Integer> check(Integer input) {
		return new OutcomeResponse<>(RESULT, Outcome.FAILED);
	}
}
