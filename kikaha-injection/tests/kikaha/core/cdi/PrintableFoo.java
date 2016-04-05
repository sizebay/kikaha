package kikaha.core.cdi;

import javax.inject.Singleton;

@Foo
@Singleton
public class PrintableFoo implements PrintableWord {

	@Override
	public String getWord() {
		return null;
	}
}
