package kikaha.cloud.consul;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.inject.*;
import kikaha.cloud.smart.ServiceRegistry;
import kikaha.config.Config;

/**
 *
 */
@Singleton
public class ConsulClient implements ServiceRegistry {

	final static String REQUEST_CHECK_STRING =
			"{\"ID\": \"{id}\",\"Name\": \"{name}\",\"Address\": \"{host}\",\"Port\": {port}, \"Tags\": {tags}," +
			"\"Check\": { \"DeregisterCriticalServiceAfter\": \"{deregister-after}m\", \"HTTP\": \"{health-check-url}\"," +
			"\"Interval\": \"{health-check-interval}s\",\"TTL\": \"{health-check-ttl}s\"}}\n";

	@Inject Config config;

	@Override
	public void registerCluster(ApplicationData applicationData) throws IOException {
		final String message = asMessage( applicationData );
		final String consulHost = config.getString( "server.consul.host", "localhost" );
		final int consulPort = config.getInteger( "server.consul.port", 8500 );
		post( "http://" + consulHost + ":" + consulPort + "/v1/agent/service/register", message );
	}

	String asMessage( final ApplicationData applicationData ) {
		final Map<String, String> params = asParams(applicationData);

		String message = REQUEST_CHECK_STRING;
		for ( final Map.Entry<String, String> entry : params.entrySet() ) {
			final String value = Optional.ofNullable( entry.getValue() ).orElse( "" );
			message = message.replace("{" + entry.getKey() + "}", value );
		}

		return message;
	}

	Map<String, String> asParams( final ApplicationData applicationData ) {
		final Map<String, String> params = new HashMap<>();
		params.put( "id", applicationData.getMachineId() );
		params.put( "name", applicationData.getName() + ":" + applicationData.getVersion() );
		params.put( "host", config.getString( "server.smart-server.application.host" ) );
		params.put( "port", config.getString( "server.smart-server.application.port" ) );
		params.put( "tags", asTagList( config.getStringList( "server.smart-server.application.tags" ) ) );
		params.put( "deregister-after", config.getString( "server.smart-server.application.deregister-critical-service-after", "90" ) );
		params.put( "health-check-interval", config.getString( "server.smart-server.application.health-check-interval" ) );
		params.put( "health-check-ttl", config.getString( "server.smart-server.application.health-check-ttl" ) );
		params.put( "health-check-url", config.getString( "server.smart-server.application.health-check-url", getHealthCheckUrl() ) );
		return params;
	}

	String getHealthCheckUrl(){
		final String healthCheckUrl = config.getString( "server.health-check.url" );
		return healthCheckUrl;
	}

	private String asTagList(List<String> stringList) {
		for ( int i=0; i<stringList.size(); i++ )
			stringList.set(i, '"' + stringList.get(i) + '"');
		return "[" + String.join(" ", stringList) + "]";
	}

	int post(String url, String msg ) throws IOException {
		final URL obj = new URL( url );
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);

		// Send post request
		try ( final DataOutputStream wr = new DataOutputStream(con.getOutputStream()) ) {
			wr.writeBytes(msg);
			wr.flush();
		}

		return con.getResponseCode();
	}
}
