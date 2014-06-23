package io.skullabs.undertow.urouting.serializers;

import io.skullabs.undertow.urouting.Mimes;
import io.skullabs.undertow.urouting.api.RoutingException;
import io.skullabs.undertow.urouting.api.Serializer;

import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import trip.spi.Name;
import trip.spi.Service;

@Service
@Name( Mimes.XML )
@SuppressWarnings("unchecked")
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
		final String lowerCase = clazz.getSimpleName().toLowerCase();
		final JAXBElement<T> element = new JAXBElement<T>( new QName(lowerCase), clazz, object );
		final Marshaller marshaller = context.createMarshaller();
        marshaller.marshal( element, output );
	}
}
