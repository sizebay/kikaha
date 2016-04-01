package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import kikaha.core.cdi.Provided;
import kikaha.core.cdi.ProvidedServices;
import kikaha.core.cdi.Qualifier;

public class DefaultFieldQualifierExtractor implements FieldQualifierExtractor {

	@Override
	public boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann) {
		return ann.isAnnotationPresent( Qualifier.class );
	}

	@Override
	public boolean isASingleElementProvider( Field field ) {
		return field.isAnnotationPresent( Provided.class );
	}

	@Override
	public boolean isAManyElementsProvider( Field field ) {
		return field.isAnnotationPresent( ProvidedServices.class );
	}
}
