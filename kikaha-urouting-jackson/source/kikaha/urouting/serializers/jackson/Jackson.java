package kikaha.urouting.serializers.jackson;

import lombok.val;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class Jackson {

	static ObjectMapper createMapper() {
		val module = new JaxbAnnotationModule();
		val mapper = new ObjectMapper();
		mapper.registerModule(module);
		return mapper;
	}
}
