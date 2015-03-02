package kikaha.urouting.samples;

import kikaha.urouting.api.GET;

public abstract class AbstractService<K> {

	abstract K getKey();

	@GET
	public void someMethodToGenerateAClassDuringCompilingTime() {

	}
}
