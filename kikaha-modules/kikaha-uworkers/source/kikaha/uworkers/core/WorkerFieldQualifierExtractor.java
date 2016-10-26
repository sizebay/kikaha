package kikaha.uworkers.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.inject.Singleton;
import kikaha.core.cdi.helpers.FieldQualifierExtractor;
import kikaha.uworkers.api.Worker;

/**
 *
 */
@Singleton
public class WorkerFieldQualifierExtractor implements FieldQualifierExtractor {

	@Override
	public boolean isASingleElementProvider(Field field) {
		return field.isAnnotationPresent(Worker.class);
	}

	@Override
	public boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann) { return false; }

	@Override
	public boolean isAManyElementsProvider(Field field) { return false; }
}
