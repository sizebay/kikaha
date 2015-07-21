package kikaha.core.security;

import io.undertow.security.idm.Account;


/**
 * Session interface based on {@code io.undertow.server.session.Session}.
 *
 * @author miere.teixeira
 */
public interface Session {

	/**
	 * @return the account representing the current logged in user. It returns
	 * null if it wasn't previously authenticated.
	 */
	Account getAuthenticatedAccount();

    /**
     * Returns a string containing the unique identifier assigned to this session.
     *
     * @return a string specifying the identifier
     *         assigned to this session
     */
    String getId();

    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     */
    Object getAttribute(String name);

    /**
     * Returns a set of <code>String</code> objects containing the names of all
     * the objects bound to this session.
     *
     * @return a set of <code>String</code> objects specifying the names of all
     * 		   the objects bound to this session
     */
    Iterable<String> getAttributeNames();

    /**
     * Binds an object to this session, using the name specified.
     * If an object of the same name is already bound to the session,
     * the object is replaced.
     *
     * @param name  the name to which the object is bound;
     *              cannot be null
     * @param value the object to be bound
     */
    void setAttribute(final String name, Object value);

    /**
     * Removes the object bound with the specified name from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * @param name the name of the object to remove from this session
     */
    Object removeAttribute(final String name);
}
