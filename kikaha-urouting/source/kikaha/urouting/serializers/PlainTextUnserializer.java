package kikaha.urouting.serializers;

import java.io.IOException;
import java.io.Reader;

import trip.spi.Singleton;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Unserializer;

@Singleton( name = Mimes.PLAIN_TEXT, exposedAs = Unserializer.class )
public class PlainTextUnserializer implements Unserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unserialize(final Reader input, final Class<T> targetClass)
			throws IOException {
		if ( !String.class.equals(targetClass) ) {
			final String message = "Can't convert a plain text into " + targetClass.getCanonicalName();
			throw new UnsupportedOperationException(message);
		}
		return (T)readAsString(input);
	}

	private String readAsString(final Reader input) throws IOException {
		final StringBuilder builder = new StringBuilder();
		final char[] buffer = new char[1024];
		int readBytes = 0;
		while ( (readBytes = input.read(buffer)) > -1 )
			builder.append(buffer, 0, readBytes);
		return builder.toString();
	}

}
