package kikaha.cloud.consul;

import java.io.*;
import java.net.*;

/**
 *
 */
public interface Http {

	static int sendRequest( String url, String method, String msg ) throws IOException {
		final HttpURLConnection con = createRequest( url, method );
		con.setDoOutput(true);

		// Send post request
		if ( msg != null )
			try ( final DataOutputStream wr = new DataOutputStream(con.getOutputStream()) ) {
				wr.writeBytes(msg);
				wr.flush();
			}

		return con.getResponseCode();
	}

	static HttpURLConnection createRequest( String url, String method ) throws IOException {
		final URL obj = new URL( url );
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod( method );
		return conn;
	}
}
