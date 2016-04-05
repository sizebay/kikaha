package kikaha.cdi.tests;

import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class PostConstructorSingletonService {

	@Getter
	final Status status = new Status();

	@javax.annotation.PostConstruct
	public void postConstructorJava() {
		status.calledPostContructJavaAnnotation++;
	}
}
