package kikaha.urouting.serializers.jackson;

import static kikaha.urouting.serializers.jackson.TestCase.readFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.*;
import kikaha.core.cdi.*;
import kikaha.core.cdi.helpers.filter.Condition;
import kikaha.core.modules.http.ContentType;
import kikaha.core.test.KikahaRunner;
import kikaha.urouting.api.*;
import kikaha.urouting.serializers.jackson.User.Address;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.xnio.ChannelListener.Setter;
import org.xnio.*;
import org.xnio.channels.ConnectedChannel;
import org.xnio.conduits.*;

@RunWith( KikahaRunner.class )
public class SerializationTests {

	final ServiceProvider provider = new DefaultServiceProvider();
	final User user = new User( "gerolasdiwn",
			new Address( "Madison Avenue", 10 ) );

	ServerConnection connection;

	@Mock
	StreamSinkConduit conduit;

	@Mock
	BlockingHttpExchange blockingExchange;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		connection = spy( new StubServerConnection() );
	}

	@Test
	@SneakyThrows
	public void grantThatSerializeItAsJSON() {
		final JSONSerializer serializer = spy((JSONSerializer)provider.load( Serializer.class, new JSONContentTypeCondition<>() ));
		final HttpServerExchange exchange = new HttpServerExchange(connection);
		doAnswer( this::ensureThatWasCorrectlySerialized ).when(serializer).send( eq(exchange), any( ByteBuffer.class ));
		serializer.serialize( user, exchange, "UTF-8" );
	}

	Void ensureThatWasCorrectlySerialized(InvocationOnMock invocation) throws Throwable {
		final ByteBuffer buffer = invocation.getArgumentAt(1, ByteBuffer.class);
		final String expected = readFile( "serialization.expected-json.json" );
		final byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		assertEquals( expected, new String( bytes ) );
		return null;
	}

	@Test
	@SneakyThrows
	public void grantThatUnserializeJSONIntoObjectAsExpected() {
		final String json = readFile( "serialization.expected-json.json" );
		final InputStream inputStream = new ByteArrayInputStream(json.getBytes());
		doReturn(inputStream).when(blockingExchange).getInputStream();
		final Unserializer unserializer = provider.load( Unserializer.class, new JSONContentTypeCondition<>() );
		final HttpServerExchange exchange = new HttpServerExchange(connection);
		exchange.startBlocking( blockingExchange );
		final User user = unserializer.unserialize( exchange, User.class, "UTF-8" );
		assertIsValidUser( user );
	}

	void assertIsValidUser( final User user ) {
		assertNotNull( user );
		assertThat( user.name, is( "gerolasdiwn" ) );
		assertNotNull( user.addresses );
		assertThat( user.addresses.size(), is( 1 ) );
		final Address address = user.addresses.get( 0 );
		assertThat( address.street, is( "Madison Avenue" ) );
		assertThat( address.number, is( 10 ) );
	}

	class JSONContentTypeCondition<T> implements Condition<T> {

		@Override
		public boolean check(T arg0) {
			final ContentType contentType = arg0.getClass().getAnnotation( ContentType.class );
			return contentType != null && Mimes.JSON.equals( contentType.value() );
		}
	}

	class StubServerConnection extends ServerConnection {

		@Override
		protected ConduitStreamSinkChannel getSinkChannel() {
			return new ConduitStreamSinkChannel( null, conduit);
		}

		@Override
		protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
			return conduit;
		}

		/* (non-Javadoc)
		 * @see org.xnio.channels.ConnectedChannel#getCloseSetter()
		 */
		@Override
		public Setter<? extends ConnectedChannel> getCloseSetter() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getBufferPool()
		 */
		@Override
		public Pool<ByteBuffer> getBufferPool() {
			return null;
		}

		@Override
		public ByteBufferPool getByteBufferPool() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getWorker()
		 */
		@Override
		public XnioWorker getWorker() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getIoThread()
		 */
		@Override
		public XnioIoThread getIoThread() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#sendOutOfBandResponse(io.undertow.server.HttpServerExchange)
		 */
		@Override
		public HttpServerExchange sendOutOfBandResponse(
				HttpServerExchange exchange) {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#isContinueResponseSupported()
		 */
		@Override
		public boolean isContinueResponseSupported() {
			return false;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#terminateRequestChannel(io.undertow.server.HttpServerExchange)
		 */
		@Override
		public void terminateRequestChannel(HttpServerExchange exchange) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#isOpen()
		 */
		@Override
		public boolean isOpen() {
			return false;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#supportsOption(org.xnio.Option)
		 */
		@Override
		public boolean supportsOption(Option<?> option) {
			return false;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getOption(org.xnio.Option)
		 */
		@Override
		public <T> T getOption(Option<T> option) throws IOException {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#setOption(org.xnio.Option, java.lang.Object)
		 */
		@Override
		public <T> T setOption(Option<T> option, T value)
				throws IllegalArgumentException, IOException {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#close()
		 */
		@Override
		public void close() throws IOException {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getPeerAddress()
		 */
		@Override
		public SocketAddress getPeerAddress() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getPeerAddress(java.lang.Class)
		 */
		@Override
		public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getLocalAddress()
		 */
		@Override
		public SocketAddress getLocalAddress() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getLocalAddress(java.lang.Class)
		 */
		@Override
		public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getUndertowOptions()
		 */
		@Override
		public OptionMap getUndertowOptions() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getBufferSize()
		 */
		@Override
		public int getBufferSize() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getSslSessionInfo()
		 */
		@Override
		public SSLSessionInfo getSslSessionInfo() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#setSslSessionInfo(io.undertow.server.SSLSessionInfo)
		 */
		@Override
		public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#addCloseListener(io.undertow.server.ServerConnection.CloseListener)
		 */
		@Override
		public void addCloseListener(CloseListener listener) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#upgradeChannel()
		 */
		@Override
		protected StreamConnection upgradeChannel() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getSourceChannel()
		 */
		@Override
		protected ConduitStreamSourceChannel getSourceChannel() {
			return null;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#isUpgradeSupported()
		 */
		@Override
		protected boolean isUpgradeSupported() {
			return false;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#isConnectSupported()
		 */
		@Override
		protected boolean isConnectSupported() {
			return false;
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#exchangeComplete(io.undertow.server.HttpServerExchange)
		 */
		@Override
		protected void exchangeComplete(HttpServerExchange exchange) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#setUpgradeListener(io.undertow.server.HttpUpgradeListener)
		 */
		@Override
		protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#setConnectListener(io.undertow.server.HttpUpgradeListener)
		 */
		@Override
		protected void setConnectListener(HttpUpgradeListener connectListener) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#maxEntitySizeUpdated(io.undertow.server.HttpServerExchange)
		 */
		@Override
		protected void maxEntitySizeUpdated(HttpServerExchange exchange) {
		}

		/* (non-Javadoc)
		 * @see io.undertow.server.ServerConnection#getTransportProtocol()
		 */
		@Override
		public String getTransportProtocol() {
			return null;
		}
	}
}
