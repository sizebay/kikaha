package kikaha.core.cdi;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class PrintableWorld implements PrintableWord {

	@Period
	@Inject
	Closure closure;

	@Override
	public String getWord() {
		return "World" + this.closure.getSentenceClosureChar();
	}
}
