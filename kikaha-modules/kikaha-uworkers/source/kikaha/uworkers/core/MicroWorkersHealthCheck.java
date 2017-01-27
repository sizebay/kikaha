package kikaha.uworkers.core;

import static java.lang.String.format;

import javax.enterprise.inject.Typed;
import javax.inject.*;
import com.codahale.metrics.health.HealthCheck;

/**
 * @author: miere.teixeira
 */
@Singleton
@Named( "uworkers-thread-pool" )
@Typed( HealthCheck.class )
public class MicroWorkersHealthCheck extends HealthCheck {

	@Inject MicroWorkersTaskDeploymentModule module;

	@Override
	protected Result check() throws Exception {
		Result result = Result.healthy();

		if ( !module.isShutdown.get() ) {
			final int totalOfActiveTasks = module.threads.getTotalOfActiveTasks();
			final int totalOfScheduledTasks = module.threads.getTotalOfScheduledTasks();
			if ( totalOfActiveTasks != totalOfScheduledTasks )
				result = Result.unhealthy(
					format( "Some workers have died. Active jobs: %d/%d", totalOfActiveTasks, totalOfScheduledTasks ) );
		}

		return result;
	}
}
