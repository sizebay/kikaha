package kikaha.urouting.serializers.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class Jackson {

	private static final ObjectMapper INSTANCE = createMapper();

	private static ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		return mapper;
	}
	
	@Produces
	public ObjectMapper objectMapper(){
		return INSTANCE;
	}
}
