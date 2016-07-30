package kikaha.mojo.sample;

public class User {

	@Visit
	String name;

	@Visit
	long id;

	@Visit
	public String getName() {
		return name;
	}
}
