package kikaha.core.api.conf;

public interface SSLConfiguration {

	String keystore();

	String truststore();

	String password();

	String certSecurityProvider();

	String keystoreSecurityProvider();

	boolean isEmpty();
}
