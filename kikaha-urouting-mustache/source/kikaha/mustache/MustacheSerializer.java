package kikaha.mustache;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;

import kikaha.core.api.conf.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Singleton
@Getter
@Accessors( fluent = true )
public class MustacheSerializer {

	private MustacheFactory mustacheFactory;
	private boolean shouldCacheTemplates;

	@Provided
	Configuration configuration;

	@PostConstruct
	public void readConfiguration() {
		shouldCacheTemplates = configuration.config().getBoolean( "server.mustache.cache-templates" );
		mustacheFactory = createMustacheFactory();
	}

	public String serialize( final MustacheTemplate object ) {
		final Writer writer = new StringWriter();
		serialize( object, writer );
		return writer.toString();
	}

	public void serialize( final MustacheTemplate object, final Writer writer ) {
		final String templateName = object.templateName();
		final Mustache compiled = mustacheFactory.compile( formatFileName( templateName ) );
		compiled.execute( writer, object.paramObject() );
	}

	private String formatFileName( final String originalTemplateName ) {
		final String sufix = originalTemplateName.contains( "." )
				? "" : ".mustache";
		final String templateName = originalTemplateName + sufix;
		return templateName;
	}

	private MustacheFactory createMustacheFactory() {
		if ( shouldCacheTemplates() )
			return new DefaultMustacheFactory( new File( configuration.resourcesPath() ) );
		return new NotCachedMustacheFactory( new File( configuration.resourcesPath() ) );
	}
}

@RequiredArgsConstructor
class NotCachedMustacheFactory implements MustacheFactory {

	final File rootDir;

	@Delegate
	public MustacheFactory createNewInstance() {
		return new DefaultMustacheFactory( rootDir );
	}
}