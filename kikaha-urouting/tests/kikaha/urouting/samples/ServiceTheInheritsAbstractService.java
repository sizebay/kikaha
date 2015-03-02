package kikaha.urouting.samples;

import kikaha.urouting.api.GET;

public class ServiceTheInheritsAbstractService extends AbstractService<Long> {

	@Override
	@GET
	Long getKey() {
		return 1l;
	}
}
