package kikaha.core.cdi;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import lombok.Getter;

/**
 *
 */
@Getter
public class InjectableClass {

	@Inject
	PrintableWord printableWord;

	@Inject
	@Typed( PrintableWord.class )
	Iterable<PrintableWord> printables;

	@Inject
	@Foo
	@Typed( PrintableWord.class )
	Iterable<PrintableWord> printableFoos;
}
