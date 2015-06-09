package kikaha.hazelcast;

import com.hazelcast.util.UuidUtil;

public abstract class SessionID {

	/**
	 * Generate a valid, pseudo-randomized, session id.<br>
	 * <br>
	 * <b>Note:</b> This code is a copy from {@code com.hazelcast.web.WebFilter}
	 *
	 * @return
	 */
	public static synchronized String generateSessionId() {
		final String id = UuidUtil.buildRandomUuidString();
		final StringBuilder sb = new StringBuilder( "HZ" );
		final char[] chars = id.toCharArray();
		for ( final char c : chars )
			if ( c != '-' )
				if ( Character.isLetter( c ) )
					sb.append( Character.toUpperCase( c ) );
				else
					sb.append( c );
		return sb.toString();
	}
}
