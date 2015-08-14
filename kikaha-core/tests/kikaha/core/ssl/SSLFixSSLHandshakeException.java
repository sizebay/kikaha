package kikaha.core.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class SSLFixSSLHandshakeException {

	/**
	 * fix for Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	 * sun.security.validator.ValidatorException: PKIX path building failed:
	 * sun.security.provider.certpath.SunCertPathBuilderException: unable to
	 * find valid certification path to requested target
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public static void applyFixPatch() throws KeyManagementException, NoSuchAlgorithmException {
		TrustManager[] trustAllCerts = new TrustManager[] { new EmptyX509TrustManager() };

		final SSLContext sc = SSLContext.getInstance( "TLS" );
		sc.init( null, trustAllCerts, new java.security.SecureRandom() );
		HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );

		HostnameVerifier allHostsValid = new EmptyHostnameVerifier();
		HttpsURLConnection.setDefaultHostnameVerifier( allHostsValid );
	}
}

class EmptyX509TrustManager implements X509TrustManager {

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	@Override
	public void checkClientTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException {
	}

	@Override
	public void checkServerTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException {
	}
}

class EmptyHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify( String hostname, SSLSession session ) {
		return true;
	}
}