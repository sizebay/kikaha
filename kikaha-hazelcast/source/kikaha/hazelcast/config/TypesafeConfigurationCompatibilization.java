package kikaha.hazelcast.config;

import com.typesafe.config.*;
import com.typesafe.config.ConfigException.Missing;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypesafeConfigurationCompatibilization {

	private static final String HAZELCAST = "hazelcast";

	final Config config;

	public void compatibilize() {
		ConfigObject parameters = getHazelcastConfigureParameters();
		memorizeProperties( parameters, HAZELCAST );
	}

	void memorizeProperties( Map<String, ConfigValue> parameters, String rootKey ) {
		for ( String key : parameters.keySet() ) {
			ConfigValue object = parameters.get( key );
			String fullKey = rootKey + "." + key;
			memorizeProperty( object, fullKey );
		}
	}

	@SuppressWarnings( "unchecked" )
	private void memorizeProperty( ConfigValue object, String fullKey ) {
		if ( Map.class.isInstance( object ) )
			memorizeProperties( (Map<String, ConfigValue>)object, fullKey );
		else
			memorizeProperty( fullKey, object.unwrapped() );
	}

	public void memorizeProperty( String fullKey, Object value ) {
		if ( value != null && System.getProperty( fullKey ) == null )
			System.setProperty( fullKey, value.toString() );
	}

	public ConfigObject getHazelcastConfigureParameters() {
		try {
			return config.getObject( HAZELCAST );
		} catch ( Missing cause ) {
			return null;
		}
	}
}
