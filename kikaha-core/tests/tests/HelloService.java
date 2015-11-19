package tests;

import trip.spi.Singleton;

@Singleton
public class HelloService {

	public String getWhoShouldISayHelloTo() {
		return "World";
	}
}
