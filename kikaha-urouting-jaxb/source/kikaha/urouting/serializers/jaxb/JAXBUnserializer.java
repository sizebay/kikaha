package kikaha.urouting.serializers.jaxb;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import kikaha.urouting.api.AbstractUnserializer;
import kikaha.urouting.api.ContentType;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Unserializer;
import trip.spi.Singleton;

@SuppressWarnings("unchecked")
@ContentType(Mimes.XML)
@Singleton( exposedAs = Unserializer.class )
public class JAXBUnserializer extends AbstractUnserializer {

	@Override
	public <T> T unserialize( Reader input, Class<T> clazz ) throws RoutingException {
		try {
			final JAXBContext context = JAXBContext.newInstance( clazz );
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal( input );
		} catch ( final JAXBException cause ) {
			throw new RoutingException(cause);
		}
	}
}
