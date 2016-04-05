package kikaha.cdi.tests;

import kikaha.core.cdi.Stateless;

@Stateless
public class PostConstructAndPreDestroyStatelessService {

	final Status status = new Status();

	@javax.annotation.PostConstruct
	public void postConstructorJava() {
		status.calledPostContructJavaAnnotation++;
	}

	@javax.annotation.PreDestroy
	public void preDestroyJava() {
		status.calledPreDestroyJavaAnnotation++;
	}

	public Status getStatus() {
		return status;
	}
}

class Status {
	int calledPreDestroyJavaAnnotation = 0;
	int calledPostContructJavaAnnotation = 0;
}