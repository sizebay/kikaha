package kikaha.urouting;

import java.io.IOException;
import java.util.Queue;
import javax.annotation.PostConstruct;
import javax.inject.*;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.util.*;
import kikaha.config.Config;
import kikaha.urouting.api.*;
import lombok.Getter;

/**
 * Provides data to a routing method.
 */
@Singleton
public class RoutingMethodDataProvider {

	@Inject
	Config kikahaConf;

	@Inject
	ConverterFactory converterFactory;

	@Inject
	ContextProducerFactory contextProducerFactory;

	@Inject
	SerializerAndUnserializerProvider serializerAndUnserializerProvider;

	@Getter
	String defaultEncoding;

	@Getter
	String defaultContentType;

	@PostConstruct
	public void readConfig() {
		defaultEncoding = kikahaConf.getString("server.urouting.default-encoding");
		defaultContentType = kikahaConf.getString("server.urouting.default-content-type");
	}

	/**
	 * Get a cookie from request converted to the {@code <T>} type as defined by
	 * {@code clazz} argument.
	 *
	 * @param exchange
	 * @param cookieParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getCookieParam(final HttpServerExchange exchange, final String cookieParam, final Class<T> clazz)
			throws ConversionException,
			InstantiationException, IllegalAccessException {
		final Cookie cookie = exchange.getRequestCookies().get(cookieParam);
		if (cookie == null)
			return null;
		final String value = cookie.getValue();
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	/**
	 * Get a query parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param exchange
	 * @param queryParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getQueryParam(final HttpServerExchange exchange, final String queryParam, final Class<T> clazz)
			throws ConversionException, InstantiationException, IllegalAccessException {
		final Queue<String> queryParams = exchange.getQueryParameters().get(queryParam);
		if (queryParams == null)
			return null;
		final String value = queryParams.peek();
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFormParam(final FormData form, final String formParam, final Class<T> clazz)
			throws ConversionException, InstantiationException, IllegalAccessException {
		final FormValue formValue = form.getFirst(formParam);
		if (formValue == null)
			return null;
		if (formValue.isFile())
			return (T) formValue.getFile();
		final String value = formValue.getValue();
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	/**
	 * Get a header parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param exchange
	 * @param headerParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getHeaderParam(final HttpServerExchange exchange, final String headerParam, final Class<T> clazz)
			throws ConversionException, InstantiationException, IllegalAccessException {
		final String value = exchange.getRequestHeaders().getFirst(headerParam);
		if (value == null)
			return null;
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	/**
	 * Get a path parameter from request converted to the {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param exchange
	 * @param pathParam
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T getPathParam(final HttpServerExchange exchange, final String pathParam, final Class<T> clazz)
			throws ConversionException, InstantiationException, IllegalAccessException {
		final PathTemplateMatch pathTemplate = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
		final String value = pathTemplate.getParameters().get(pathParam);
		return converterFactory.getConverterFor(clazz).convert(value);
	}

	/**
	 * Get the body of current request and convert to {@code <T>} type as
	 * defined by {@code clazz} argument.
	 *
	 * @param exchange
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public <T> T getBody(final HttpServerExchange exchange, final Class<T> clazz) throws IOException {
		return getBody(exchange, clazz, getDefaultContentType());
	}

	/**
	 * Get the body of current request and convert to {@code <T>} type as
	 * defined by {@code clazz} argument.<br>
	 * <br>
	 * <p>
	 * It searches for {@link Unserializer} implementations to convert sent data
	 * from client into the desired object. The "Content-Type" header is the
	 * information needed to define which {@link Unserializer} should be used to
	 * decode the sent data into an object. When no {@link Unserializer} is
	 * found it uses the {@code defaultConsumingContentType} argument to seek
	 * another one. It throws {@link RoutingException} when no decoder was
	 * found.
	 *
	 * @param exchange
	 * @param clazz
	 * @param defaultConsumingContentType
	 * @return
	 * @throws IOException
	 */
	public <T> T getBody(final HttpServerExchange exchange, final Class<T> clazz, final String defaultConsumingContentType)
			throws IOException {
		String contentEncoding = exchange.getRequestHeaders().getFirst(Headers.CONTENT_ENCODING_STRING);
		if (contentEncoding == null)
			contentEncoding = getDefaultEncoding();
		String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE_STRING);
		if (contentType == null)
			contentType = defaultConsumingContentType;
		return unserializeReceivedBodyStream(exchange, clazz, contentEncoding, contentType);
	}

	private <T> T unserializeReceivedBodyStream(
			final HttpServerExchange exchange, final Class<T> clazz,
            final String contentEncoding, final String contentType) throws IOException
	{
		if (!exchange.isBlocking())
			exchange.startBlocking();
		final Unserializer unserializer = serializerAndUnserializerProvider.getUnserializerFor(contentType);
		return unserializer.unserialize(exchange, clazz, contentEncoding);
	}

	/**
	 * Retrieve ( or produces ) a request-time object.
	 *
	 * @param exchange
	 * @param clazz
	 * @return
	 * @throws RoutingException
	 */
	public <T> T getData(final HttpServerExchange exchange, final Class<T> clazz) throws RoutingException {
		final ContextProducer<T> producerFor = contextProducerFactory.producerFor(clazz);
		if (producerFor != null)
			return producerFor.produce(exchange);
		throw new RoutingException("No context provider for " + clazz.getCanonicalName());
	}
}
