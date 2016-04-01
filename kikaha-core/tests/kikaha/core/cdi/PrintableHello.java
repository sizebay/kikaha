package kikaha.core.cdi;

import lombok.Getter;

@Getter
public class PrintableHello implements Printable {

	@Provided PrintableWord word;

	@Override
	public String toString() {
		return "Hello " + word.getWord();
	}
}
