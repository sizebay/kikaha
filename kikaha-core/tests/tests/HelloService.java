package tests;

import javax.inject.Singleton;

@Singleton
public class HelloService {

	public String getWhoShouldISayHelloTo() {
		return "World";
	}
}
