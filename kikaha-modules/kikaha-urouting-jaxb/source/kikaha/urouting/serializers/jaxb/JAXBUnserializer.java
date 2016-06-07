package kikaha.urouting.serializers.jaxb;

import kikaha.core.modules.http.ContentType;
import kikaha.urouting.api.*;

import javax.enterprise.inject.Typed;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;

@SuppressWarnings("unchecked")
@ContentType(Mimes.XML)
@Singleton
@Typed( Unserializer.class )
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
