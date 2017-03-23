package kikaha.uworkers.core;

import kikaha.config.Config;
import lombok.*;

/**
 *
 */
public interface EndpointConfig {

	Config getConfig();

	EndpointFactory getEndpointFactory();

	String getEndpointName();

	int getParallelism();

	default EndpointConfig withFallbackTo( @NonNull EndpointConfig fallbackConfig ) {
		return new WrappedEndpointConfig( this, fallbackConfig );
	}

	@Getter
	@RequiredArgsConstructor
	@EqualsAndHashCode
	class DefaultEndpointConfig implements EndpointConfig {

		final Config config;
		final EndpointFactory endpointFactory;
		final String endpointName;

		public int getParallelism(){
			return config.getInteger( "parallelism" );
		}
	}
}

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
class WrappedEndpointConfig implements EndpointConfig {

	final EndpointConfig target;
	final EndpointConfig fallback;

	@Override
	public Config getConfig() {
		return target.getConfig();
	}

	@Override
	public String getEndpointName() {
		return target.getEndpointName();
	}

	@Override
	public EndpointFactory getEndpointFactory() {
		EndpointFactory endpointFactory = target.getEndpointFactory();
		if ( endpointFactory == null )
			endpointFactory = fallback.getEndpointFactory();
		return endpointFactory;
	}

	@Override
	public int getParallelism() {
		int parallelism = target.getParallelism();
		if ( parallelism == 0 )
			parallelism = fallback.getParallelism();
		return parallelism;
	}
}