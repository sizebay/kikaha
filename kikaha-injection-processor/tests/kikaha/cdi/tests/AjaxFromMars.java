package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

import kikaha.cdi.tests.ann.Ajax;

@Ajax
@Singleton
@Typed( Hero.class )
public class AjaxFromMars implements Hero, World {

	@Inject
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
