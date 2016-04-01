package kikaha.core.cdi;

import lombok.Getter;

@Getter
public class Readable {

	@Provided
	PrintableWord word;

}
