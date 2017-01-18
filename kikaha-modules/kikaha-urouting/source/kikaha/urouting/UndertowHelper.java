package kikaha.urouting;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.AttachmentKey;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A very tiny API to deal with Undertow's low-level API.
 */
@Singleton
public class UndertowHelper {

	private static final AttachmentKey<byte[]> ATTACHMENT_RAW_BYTES = AttachmentKey.create(byte[].class);

	@Inject RoutingMethodParameterReader parameterReader;
	@Inject RoutingMethodResponseWriter responseWriter;
	@Inject RoutingMethodExceptionHandler exceptionHandler;

	/**
	 * Create a simplified version of a {@link HttpServerExchange}.
	 *
	 * @param exchange
	 * @return the simplified version of {@link HttpServerExchange}.
	 */
	public SimpleExchange simplify(HttpServerExchange exchange) {
		return SimpleExchange.wrap( exchange, parameterReader, responseWriter, exceptionHandler );
	}

	/**
	 * Retrieve the memorized body data.
	 *
	 * @param exchange
	 * @return
	 */
	public static byte[] getReadBodyData( final HttpServerExchange exchange ){
		return exchange.getAttachment( ATTACHMENT_RAW_BYTES );
	}

	public static void storeReadBodyData( final HttpServerExchange exchange, final byte[] data ){
		exchange.putAttachment( ATTACHMENT_RAW_BYTES, data );
	}

	/**
	 * Retrieve the {@link FormData} if previously stored on the {@link HttpServerExchange}.
	 * @param exchange
	 * @return
	 */
	public static FormData getFormData(final HttpServerExchange exchange ) {
		return exchange.getAttachment( FormDataParser.FORM_DATA );
	}
}
