package kikaha.core.modules.http.ssl;

import io.undertow.Undertow;
import kikaha.config.Config;
import kikaha.core.DeploymentContext;
import kikaha.core.test.Exposed;
import kikaha.core.test.KikahaRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xnio.OptionMap;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HttpsModule}.
 */
@RunWith(KikahaRunner.class)
public class HttpsModuleTest {

    Undertow.Builder server = Undertow.builder();
    Exposed exposedServer = new Exposed( server );

    @Inject HttpsModule httpsModule;
    @Mock SSLContextFactory sslContextFactory;
    @Mock Config config, httpConfig;
    @Mock DeploymentContext context;

    @Before
    public void configureMocks(){
        MockitoAnnotations.initMocks( this );
        httpsModule.sslContextFactory = sslContextFactory;
        httpsModule.config = config;
        httpsModule = spy( httpsModule );
        doReturn( httpConfig ).when( config ).getConfig( "server.https" );
        doReturn( "localhost" ).when( httpConfig ).getString( "host" );
        doReturn( 4443 ).when( httpConfig ).getInteger( "port" );
    }

    @Test
    public void ensureCanNotInitializeModuleIfModuleIsNotEnabled() throws IOException {
        doReturn( false ).when( httpConfig ).getBoolean( "enabled" );
        httpsModule.load( server, context );
        verify( httpsModule, never() ).loadHttpsListener( any(), any() );
        verify( httpsModule, never() ).deployHttpToHttps( any(), any() );
        verify( httpsModule, never() ).setupHttp2( any(), any() );
        verifyZeroInteractions( context );
    }

    @Test
    public void ensureCanInitializeModuleIfItIsEnabled() throws IOException {
        doReturn( true ).when( httpConfig ).getBoolean( "enabled" );
        httpsModule.load( server, context );
        verify( httpsModule, times(1) ).loadHttpsListener( any(), any() );
        verify( httpsModule, times(1) ).deployHttpToHttps( any(), any() );
        verify( httpsModule, times(1) ).setupHttp2( any(), any() );
    }

    @Test
    public void ensureSetupHttpsListener() throws IOException {
        doReturn(true).when(httpConfig).getBoolean("enabled");
        httpsModule.load( server, context );

        final List<?> listeners = exposedServer.getFieldValue( "listeners", List.class );
        assertEquals( 1, listeners.size() );

        final Exposed exposedListener = new Exposed( listeners.get(0) );
        assertEquals( Undertow.ListenerType.HTTPS, exposedListener.getFieldValue( "type", Undertow.ListenerType.class ));
        assertEquals( "localhost", exposedListener.getFieldValue( "host", String.class ));
        assertEquals( 4443, exposedListener.getFieldValue( "port", int.class ), 0);
    }

    @Test
    public void ensureDeployHttpsAutoRedirectionHandler() throws IOException {
        doReturn(true).when(httpConfig).getBoolean("enabled");
        doReturn(true).when(httpConfig).getBoolean("redirect-http-to-https");
        httpsModule.load( server, context );

        verify( context ).rootHandler();
        verify( context ).rootHandler( any( AutoHTTPSRedirectHandler.class ) );
    }

    @Test
    public void ensureCanSetupHttp2() throws IOException {
        doReturn(true).when(httpConfig).getBoolean("enabled");
        doReturn(true).when(httpConfig).getBoolean("http2-mode");
        httpsModule.load( server, context );

        final OptionMap serverOptions = exposedServer.getFieldValue( "serverOptions", OptionMap.Builder.class ).getMap();
        assertTrue( serverOptions.get( ENABLE_HTTP2 ) );
    }
}
