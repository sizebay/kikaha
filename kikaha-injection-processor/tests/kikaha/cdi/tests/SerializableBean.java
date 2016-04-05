package kikaha.cdi.tests;

import javax.enterprise.inject.Typed;

import kikaha.core.cdi.Stateless;

@Stateless
@Typed( Bean.class )
public class SerializableBean implements Bean {

}
