package kikaha.core.cdi;

public class ApplicationRunner {

	final ServiceProvider provider = new DefaultServiceProvider();
	Application application = provider.load( Application.class );

	public void run() throws Exception {
		application.run();
	}

	public static void main( String[] args ) throws Exception {
		new ApplicationRunner().run();
	}
}
