package kikaha.hazelcast.mapstore;

import java.io.Serializable;
import java.util.Set;

public interface Store extends Serializable {
	Set<String> loadAllKeys();
}