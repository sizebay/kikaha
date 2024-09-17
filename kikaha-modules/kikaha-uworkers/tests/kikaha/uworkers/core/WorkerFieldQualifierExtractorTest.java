package kikaha.uworkers.core;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import kikaha.uworkers.api.*;
import org.junit.*;

/**
 * Unit tests for {@code WorkerFieldQualifierExtractor}.
 */
public class WorkerFieldQualifierExtractorTest {

	final WorkerFieldQualifierExtractor extractor = new WorkerFieldQualifierExtractor();

	Field fieldWithCorrectWorkerRef;
	Field fieldWithInvalidWorkerRef;

	@Before
	public void configureTest() throws NoSuchFieldException {
		final Class<?> clazz = HypotheticalClassWithWorkerReferences.class;
		fieldWithCorrectWorkerRef = clazz.getDeclaredField( "workerRef" );
		fieldWithInvalidWorkerRef = clazz.getDeclaredField( "workerRefNotAnnotated" );
	}

	@Test
	public void ensureCanIdentifyASingleElementProvider() throws Exception {
		assertTrue( extractor.isASingleElementProvider( fieldWithCorrectWorkerRef ) );
	}

	@Test
	public void ensureCanIdentifyFieldIsNotASingleElementProvider() throws Exception {
		assertFalse( extractor.isASingleElementProvider( fieldWithInvalidWorkerRef ) );
	}

	@Test
	public void ensureCanIdentifyFieldIsNotAnnotatedWithQualifierAnnotation() throws Exception {
		assertFalse( extractor.isAnnotatedWithQualifierAnnotation( Worker.class ) );
	}

	@Test
	public void ensureCanIdentifyFieldIsNotAManyElementsProvider() throws Exception {
		assertFalse( extractor.isAManyElementsProvider( fieldWithCorrectWorkerRef ) );
		assertFalse( extractor.isAManyElementsProvider( fieldWithInvalidWorkerRef ) );
	}

}

class HypotheticalClassWithWorkerReferences {

	@Worker( value = "fake-worker" )
	WorkerRef workerRef;
	WorkerRef workerRefNotAnnotated;

}