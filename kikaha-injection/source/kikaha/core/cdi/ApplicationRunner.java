package kikaha.core.cdi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationRunner {

	final DefaultServiceProvider provider = new DefaultServiceProvider();

	public void run() throws Exception {
		provider.loadAllCustomClassConstructors();
		loadApplication().run();
	}

	Application loadApplication(){
		return provider.load( Application.class );
	}

	public static void main( String[] args ) throws Exception {
		final long start = System.currentTimeMillis();
		new ApplicationRunner().run();
		final long elapsed = System.currentTimeMillis() - start;
		log.info("Application started in " + elapsed + "ms.");
	}
}
