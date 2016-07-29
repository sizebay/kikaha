package kikaha.core.modules.security;

import java.net.*;
import java.util.*;

/**
 * A helper class to generate session ids.
 */
public interface SessionIdGenerator {

	String MAC_ADDRESS = retrieveCurrentMacAddress();

	/**
	 * Generate a new session id.
	 * @return
	 */
	static String generate(){
		return MAC_ADDRESS + UUID.randomUUID().toString().replace("-","");
	}

	/**
	 * Retrieve the first MAC Address found at the machine.
	 *
	 * @return
	 */
	static String retrieveCurrentMacAddress(){
		try {
			final NetworkInterface networkInterface = getNetworkInterface();
			return new String( convertMACBytesToString( networkInterface.getHardwareAddress() ) );
		} catch ( final SocketException e ) {
			throw new RuntimeException(e);
		}
	}

	static NetworkInterface getNetworkInterface() throws SocketException {
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while ( networkInterfaces.hasMoreElements() ) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			final byte[] hardwareAddress = networkInterface.getHardwareAddress();
			if ( hardwareAddress != null && hardwareAddress.length > 4 )
				return networkInterface;
		}
		return null;
	}

	static String convertMACBytesToString( byte[] mac ){
		final StringBuilder buffer = new StringBuilder();
		for (final byte element : mac)
			buffer.append(String.format("%02X", element));
		return buffer.toString();
	}
}
