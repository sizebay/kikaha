package kikaha.core.cdi.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Qualifier;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

public class QualifierExtractorTest {

	InjectableDataExtractor extractor;

	@MyQualifiedAnn
	String qualifiedString;

	@Before
	public void setup(){
		extractor = new InjectableDataExtractor( Arrays.asList( new DefaultFieldQualifierExtractor() ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCanFindAnnotationsWithDefaultQualifier(){
		final Field field = QualifierExtractorTest.class.getDeclaredField("qualifiedString");
		final Collection<Class<? extends Annotation>> qualifiers = extractor.extractQualifiersFrom(field);
		for ( final Class<? extends Annotation> ann : qualifiers )
			assertEquals( MyQualifiedAnn.class, ann );
	}

	@Test
	public void ensureThatCanDetecteQualifierAnnotations(){
		assertTrue( extractor.isAnnotatedWithQualifierAnnotation(MyQualifiedAnn.class) );
	}
}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@interface MyQualifiedAnn {

}