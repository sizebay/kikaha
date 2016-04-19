package kikaha.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import kikaha.config.Config;
import kikaha.core.test.KikahaRunner;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.io.File;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith( KikahaRunner.class )
public class MustacheSerializerTest {

	@Inject
	MustacheSerializer serializer;

	@Mock
	Config config;

	@Before
	public void provideDependencies() {
		MockitoAnnotations.initMocks(this);
		doReturn( new File( "tests-resources" ).getAbsolutePath() ).when( config ).getString( "server.static.location" );
		serializer = spy( serializer );
		serializer.config = config;
	}

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateWithoutRetrieveFromCache() {
		doReturn( false ).when( config ).getBoolean( eq( "server.mustache.cache-templates" ) );
		serializer.readConfiguration();

		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		assertTrue( serializer.mustacheFactory() instanceof NotCachedMustacheFactory );
	}

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateRetrievingFromCache() {
		doReturn( true ).when( config ).getBoolean( eq( "server.mustache.cache-templates" ) );
		serializer.readConfiguration();

		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		assertTrue( serializer.mustacheFactory() instanceof DefaultMustacheFactory );
	}

	MustacheResponse readSimulatedResponse() {
		val user = new User();
		user.setName( "Poppins" );
		val response = MustacheResponse.ok()
			.templateName( "sample" )
			.paramObject( user );
		return response;
	}
}
