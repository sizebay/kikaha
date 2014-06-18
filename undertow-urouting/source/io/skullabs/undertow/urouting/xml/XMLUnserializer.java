package io.skullabs.undertow.urouting.xml;

import io.skullabs.undertow.urouting.Mimes;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import trip.spi.Name;
import trip.spi.Service;
import urouting.api.RoutingException;
import urouting.api.Unserializer;

@Service
@Name( Mimes.XML )
@SuppressWarnings("unchecked")
public class XMLUnserializer implements Unserializer {

	@Override
	public <T> T unserialize( Reader input, Class<T> clazz ) throws RoutingException {
		try {
			final JAXBContext context = JAXBContext.newInstance( clazz );
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal( input );
		} catch ( JAXBException cause ) {
			throw new RoutingException(cause);
		}
	}
}
