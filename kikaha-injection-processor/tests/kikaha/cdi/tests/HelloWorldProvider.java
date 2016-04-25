package kikaha.cdi.tests;

import javax.enterprise.inject.Produces;

import kikaha.cdi.tests.ann.Foo;

public class HelloWorldProvider {

	@Produces
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}

}
