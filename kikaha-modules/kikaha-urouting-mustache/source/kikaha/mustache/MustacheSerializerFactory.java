package kikaha.mustache;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class MustacheSerializerFactory {

	@Inject
	MustacheSerializer serializer;

	public MustacheSerializer serializer() {
		return serializer;
	}
}
