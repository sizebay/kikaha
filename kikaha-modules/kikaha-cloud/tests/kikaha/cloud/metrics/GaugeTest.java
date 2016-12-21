package kikaha.cloud.metrics;

import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GaugeTest {

	static final int EXPECTED_RPS = 10;

	@Test
	@SneakyThrows
	public void ensureThatIsAbleToRetrieveTheGaugeDataAsExpected(){
		final AtomicLong counter = new AtomicLong( 0l );
		final Gauge gauge = new Gauge("test","default", () -> (double) counter.get());

		for ( int i=0; i<EXPECTED_RPS; i++ )
			counter.incrementAndGet();

		assertEquals( 10, gauge.getData().getValue(), 0 );
	}
}
