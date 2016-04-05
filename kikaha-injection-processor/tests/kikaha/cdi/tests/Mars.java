package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

@Singleton
@Typed( World.class )
public class Mars implements World {

	@Override
	public String getWorld() {
		return "Mars";
	}
}
