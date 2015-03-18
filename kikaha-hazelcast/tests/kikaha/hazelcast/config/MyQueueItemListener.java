package kikaha.hazelcast.config;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

public class MyQueueItemListener implements ItemListener<String> {

	@Override
	public void itemAdded( ItemEvent<String> item ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemRemoved( ItemEvent<String> item ) {
		// TODO Auto-generated method stub

	}
}
