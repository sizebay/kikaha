package io.skullabs.undertow.standalone.xml;

import io.skullabs.undertow.standalone.api.Unserializer;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import trip.spi.Name;
import trip.spi.Service;

@Service
@Name( "text/xml" )
@SuppressWarnings("unchecked")
public class XMLUnserializer implements Unserializer {

	@Override
	public <T> T unserialize( Reader input, Class<T> clazz ) {
		try {
			final JAXBContext context = JAXBContext.newInstance( clazz );
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal( input );
		} catch ( JAXBException cause ) {
			cause.printStackTrace();
			return null;
		}
	}
}
