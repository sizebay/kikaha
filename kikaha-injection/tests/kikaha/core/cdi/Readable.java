package kikaha.core.cdi;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class Readable {

	@Inject
	PrintableWord word;

}
