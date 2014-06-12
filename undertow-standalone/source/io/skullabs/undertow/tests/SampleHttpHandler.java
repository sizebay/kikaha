package io.skullabs.undertow.tests;

import io.skullabs.undertow.standalone.api.WebResource;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import trip.spi.Service;

@Service
@WebResource( "/hello" )
public class SampleHttpHandler implements HttpHandler {

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().add( new HttpString("Content-Type"), "text/xml" );
		final WritableByteChannel responseChannel = exchange.getResponseChannel();
		final Writer writer = Channels.newWriter(responseChannel, "UTF-8");
		writer.write(
			"<root>"
				+ "<message-type>Hello</message-type>"
				+ "<to>World</to>"
			+ "</root>");
		writer.flush();
	}
}