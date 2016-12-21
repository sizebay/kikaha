package kikaha.cloud.metrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 *
 */
@RequiredArgsConstructor
public class Gauge implements ReadOnlyData<Gauge.GaugeData>, Metric {

	@Getter final String name;
	@Getter final String namespace;
	final Supplier<Double> supplier;

	@Override
	public GaugeData getData() {
		return new GaugeData(
			name, namespace, supplier.get()
		);
	}

	@Getter
	@RequiredArgsConstructor
	public static class GaugeData implements Metric {
		final String name;
		final String namespace;
		final double value;
	}
}
