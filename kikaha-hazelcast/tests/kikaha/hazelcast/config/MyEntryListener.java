package kikaha.hazelcast.config;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;

public class MyEntryListener implements EntryListener<String, String> {

	@Override
	public void entryAdded( EntryEvent<String, String> event ) {
		// TODO Auto-generated method stub
	}

	@Override
	public void entryRemoved( EntryEvent<String, String> event ) {
		// TODO Auto-generated method stub
	}

	@Override
	public void entryUpdated( EntryEvent<String, String> event ) {
		// TODO Auto-generated method stub
	}

	@Override
	public void entryEvicted( EntryEvent<String, String> event ) {
		// TODO Auto-generated method stub
	}
}
