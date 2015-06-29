package kikaha.urouting.serializers.jackson;

import lombok.val;
import trip.spi.Producer;
import trip.spi.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class Jackson {

	private static final ObjectMapper INSTANCE = createMapper();

	private static ObjectMapper createMapper() {
		val mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		return mapper;
	}
	
	@Producer
	public ObjectMapper objectMapper(){
		return INSTANCE;
	}
}
