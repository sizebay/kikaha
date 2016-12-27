package kikaha.core.modules.http.ssl;

import kikaha.core.cdi.DefaultServiceProvider;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

/**
 * An utility to test if the Keystore is correctly configured.
 */
@Slf4j
public class KeystoreTester {

    @Inject SSLContextFactory factory;

    public static void main( String[] args ) throws IOException {
        final DefaultServiceProvider serviceProvider = new DefaultServiceProvider();
        final KeystoreTester tester = serviceProvider.load(KeystoreTester.class);
        tester.loadCertificate( args[0], args[1] );
    }

    private void loadCertificate(String certificateName, String password) throws IOException {
        factory.createSSLContext( certificateName, null, password );
    }
}
