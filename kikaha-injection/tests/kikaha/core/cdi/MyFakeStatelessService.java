package kikaha.core.cdi;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Getter
@GeneratedFromStatelessService
@Singleton
public class MyFakeStatelessService extends Readable {

	@Inject
	Printable printable;
}