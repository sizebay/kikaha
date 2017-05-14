package kikaha.cloud.metrics;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import com.codahale.metrics.health.HealthCheck;

/**
 *
 */
@Singleton
@Typed( HealthCheck.class )
public class MetricStoreHealthCheck extends HealthCheck {

	volatile Result lastStatus = Result.healthy();

	@Override
	protected Result check() throws Exception {
		return lastStatus;
	}

	public void setUnhealthy( Throwable reason ) {
		lastStatus = Result.unhealthy( reason );
	}

	public void setHealthy(){
		if ( !lastStatus.isHealthy() )
			lastStatus = Result.healthy();
	}
}
