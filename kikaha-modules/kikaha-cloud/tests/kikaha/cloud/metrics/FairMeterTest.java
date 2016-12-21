package kikaha.cloud.metrics;

import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class FairMeterTest {

	static final int EXPECTED_RPS = 10, PARALLELISM = Runtime.getRuntime().availableProcessors() * 2;
	static final long[] ELAPSED_TIMES = new long[]{ 1, 2, 3, 4, 5, 1, 2, 3, 4, 5 };
	final ExecutorService executor = Executors.newFixedThreadPool( PARALLELISM );

	@Test
	@SneakyThrows
	public void ensureThatIsAbleToRetrieveTheMeterDataAsExpected(){
		final FairMeter meter = new FairMeter( "meter", "default" );

		for ( int i=0; i<EXPECTED_RPS; i++ ) {
			final long elapsedTime = ELAPSED_TIMES[i];
			executor.submit(() -> {
				meter.addElapsedTime( elapsedTime );
				meter.mark();
			});
		}

		Thread.sleep( 1000L );
		final FairMeter.MeterData meterData = meter.getData();
		assertEquals( 10, meterData.rate(), 0.9 );
		assertEquals( 3, meterData.responseTime(), 0 );
	}

	@Test
	@SneakyThrows
	public void ensureThatIsAbleToRegisterElapsedTime(){
		final FairMeter meter = new FairMeter( "meter","default" );

		for ( int i=0; i<EXPECTED_RPS; i++ )
			executor.submit(meter::mark);

		Thread.sleep( 1000L );
		assertEquals( 10, meter.getData().rate(), 0.9 );
	}
}
