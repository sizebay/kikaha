package kikaha.urouting.serializers;

import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Singleton;

@SuppressWarnings("unchecked")
@Singleton( name = Mimes.XML, exposedAs = Serializer.class )
public class XMLSerializer implements Serializer {

	@Override
	public <T> void serialize(T object, Writer output) throws RoutingException {
        try {
			Class<T> clazz = (Class<T>) object.getClass();
			serialize(clazz, object, output);
		} catch (JAXBException cause) {
			throw new RoutingException(cause);
		}
	}

	<T> void serialize(Class<T> clazz, T object, Writer output) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance( clazz );
		final String rootElementName = extractRootElementName( clazz );
		final JAXBElement<T> element = new JAXBElement<T>( new QName(rootElementName), clazz, object );
		final Marshaller marshaller = context.createMarshaller();
        marshaller.marshal( element, output );
	}

	String extractRootElementName( Class<?> clazz ) {
		final XmlRootElement rootElement = clazz.getAnnotation( XmlRootElement.class );
		if ( rootElement != null && !rootElement.name().isEmpty() )
			return rootElement.name();
		return clazz.getSimpleName().toLowerCase();
	}
}