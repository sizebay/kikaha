package kikaha.cdi.tests.concurrency;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import kikaha.cdi.tests.ann.Names;
import lombok.val;

@Singleton
public class NameProducer {

	@Produces
	@Names
	public List<String> produceNames() {
		val list = new ArrayList<String>();
		list.add( "Ereim" );
		list.add( "Leinil" );
		list.add( "Nedleh" );
		list.add( "Annailop" );
		return list;
	}
}
