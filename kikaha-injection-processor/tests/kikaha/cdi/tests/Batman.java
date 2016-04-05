package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

import kikaha.cdi.tests.ann.DarkKnight;

@DarkKnight
@Singleton
@Typed( Hero.class )
public class Batman implements Hero, World {

	@Inject
	@Typed( World.class )
	Mars mars;

	@Override
	public String getWorld() {
		return mars.getWorld();
	}
}
