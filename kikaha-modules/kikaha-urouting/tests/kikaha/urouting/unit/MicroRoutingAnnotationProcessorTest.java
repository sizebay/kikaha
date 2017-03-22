package kikaha.urouting.unit;

import static kikaha.apt.APT.extractPackageName;
import static kikaha.urouting.apt.MicroRoutingAnnotationProcessor.generateHttpPath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MicroRoutingAnnotationProcessorTest {

	@Test
	public void ensureThatFormatsHttpPathAsExpected() {
		assertThat( generateHttpPath( "", "" ), is( "/" ) );
		assertThat( generateHttpPath( "/", "" ), is( "/" ) );
		assertThat( generateHttpPath( "/hello", "" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "/hello", "/" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "hello", "" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "", "hello" ), is( "/hello/" ) );
		assertThat( generateHttpPath( "hello", "world/{id}" ), is( "/hello/world/{id}/" ) );
	}

	@Test
	public void ensureThatExtractPackageNameAsExpected() {
		String canonicalName = String.class.getCanonicalName();
		String packageName = String.class.getPackage().getName();
		assertThat( extractPackageName( canonicalName ), is( packageName ) );
	}
}
