package kikaha.uworkers.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import java.io.StringWriter;
import java.nio.file.*;
import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import lombok.SneakyThrows;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link MicroWorkerClassGenerator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MicroWorkerClassGeneratorTest {

	final StringWriter writer = new StringWriter();
	@Mock JavaFileObject sourceFile;
	@Mock Filer filer;
	MicroWorkerClassGenerator generator;

	@Before
	@SneakyThrows
	public void configureFiler(){
		doReturn( sourceFile ).when( filer ).createSourceFile( anyString() );
		doReturn( writer ).when( sourceFile ).openWriter();
		generator = new MicroWorkerClassGenerator( filer );
	}

	@Test
	@SneakyThrows
	public void ensureCanGenerateARawListenerClass(){
		final MicroWorkerListenerClass workerListenerClass = new MicroWorkerListenerClass(
			"io.kikaha.sample", "TargetClass", "methodName", true);
		generator.generate( workerListenerClass );

		final String expectedGeneratedClass = readFile("expected-generated-raw-class.java");
		assertEquals( expectedGeneratedClass, writer.toString() );
	}

	@Test
	@SneakyThrows
	public void ensureCanGenerateAListenerClass(){
		final MicroWorkerListenerClass workerListenerClass = new MicroWorkerListenerClass(
				"io.kikaha.sample", "TargetClass", "methodName", false);
		generator.generate( workerListenerClass );

		final String expectedGeneratedClass = readFile("expected-generated-class.java");
		assertEquals( expectedGeneratedClass, writer.toString() );
	}

	@SneakyThrows
	String readFile( String fileName ){
		final Path path = Paths.get( "tests-resources/" + fileName);
		return new String( Files.readAllBytes( path ) );
	}
}
