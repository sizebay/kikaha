package kikaha.cloud.smart;

import java.net.*;
import java.util.Enumeration;
import javax.inject.*;
import kikaha.config.Config;

/**
 *
 */
@Singleton
public class DefaultLocalAddressResolver implements LocalAddressResolver {

	@Inject Config config;

	@Override
	public String getLocalAddress() {
		try {
			final NetworkInterface networkInterface = getNetworkInterface();
			return getLocalAddressFrom( networkInterface ).replace("/", "");
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}
	}

	String getLocalAddressFrom( final NetworkInterface networkInterface ) throws SocketException {
		final boolean useIpv4Only = config.getBoolean("server.cloud.local-address.ipv4-only", true);
		final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
		while ( addresses.hasMoreElements() ) {
			final InetAddress inetAddress = addresses.nextElement();
			if (!useIpv4Only || inetAddress instanceof Inet4Address)
				return inetAddress.toString();
		}
		throw new SocketException( "Could not find the expected InetAddress." );
	}

	NetworkInterface getNetworkInterface() throws SocketException {
		final String interfaceName = config.getString("server.cloud.local-address.default-interface", "eth0");
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		while ( networkInterfaces.hasMoreElements() ) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			if ( interfaceName.equals( networkInterface.getName() ) )
				return networkInterface;
		}

		throw new SocketException( "Could not find the expected NetworkInterface." );
	}
}
