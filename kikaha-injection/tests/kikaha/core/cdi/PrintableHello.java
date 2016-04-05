package kikaha.core.cdi;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class PrintableHello implements Printable {

	@Inject PrintableWord word;

	@Override
	public String toString() {
		return "Hello " + word.getWord();
	}
}
