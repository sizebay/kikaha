package kikaha.hazelcast.config;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;

public class MyEntryListener implements EntryListener<String, String> {

	@Override
	public void entryAdded( EntryEvent<String, String> arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entryEvicted( EntryEvent<String, String> arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entryRemoved( EntryEvent<String, String> arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entryUpdated( EntryEvent<String, String> arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mapCleared( MapEvent arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mapEvicted( MapEvent arg0 ) {
		// TODO Auto-generated method stub

	}
}
