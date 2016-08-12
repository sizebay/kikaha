package kikaha.core.cdi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import lombok.RequiredArgsConstructor;

/**
 * Helper class that is able to extract any useful data that should be needed during the
 * Dependency Injection process.
 */
@RequiredArgsConstructor
public class InjectableDataExtractor {

	final Iterable<FieldQualifierExtractor> extractors;

	/**
	 * Extract Qualifier Annotation from a given {@code field}.
	 *
	 * @param field
	 * @return
	 */
	public Collection<Class<? extends Annotation>> extractQualifiersFrom( Field field ){
		final Collection<Class<? extends Annotation>> anns = new TinyList<>();
		for ( final FieldQualifierExtractor extractor : extractors )
			anns.addAll( extractor.extractQualifiersFrom(field) );
		return anns;
	}

	/**
	 * Check if the given Annotation {@code ann} is a Qualifier Annotation.
	 *
	 * @param ann
	 * @return
	 */
	public boolean isAnnotatedWithQualifierAnnotation( Class<? extends Annotation> ann ){
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isAnnotatedWithQualifierAnnotation(ann) )
				return true;
		return false;
	}

	/**
	 * Check if the given {@code field} should hold a Managed Object.
	 *
	 * @param field
	 * @return
	 */
	public boolean isASingleElementProvider( Field field ) {
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isASingleElementProvider( field ) )
				return true;
		return false;
	}

	/**
	 * Check if the given {@code field} should hold one or more Managed Objects.
	 *
	 * @param field
	 * @return
	 */
	public boolean isAManyElementsProvider( Field field ) {
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isAManyElementsProvider( field ) )
				return true;
		return false;
	}
}
