package kikaha.cloud.smart;

/**
 * Identify to which IP/FQDNS address the current machine belongs.
 */
public interface LocalAddressResolver {

	/**
	 * @return the IP/FQDNS address the current machine belongs
	 */
	String getLocalAddress();
}
