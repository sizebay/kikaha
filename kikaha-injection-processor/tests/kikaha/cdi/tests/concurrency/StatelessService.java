package kikaha.cdi.tests.concurrency;

import java.util.List;

import javax.inject.Inject;

import kikaha.cdi.tests.ann.Names;
import kikaha.core.cdi.Stateless;
import lombok.val;

@Stateless
public class StatelessService {

	@Inject
	@Names
	List<String> names;

	@Inject
	Printer printer;

	void printNames() {
		val builder = new StringBuilder();
		for ( val name : names )
			builder.append( name ).append( ' ' );
		printer.print( builder.toString() );
	}
}
