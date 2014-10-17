package kikaha.urouting;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class WebSocketClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;

	public WebSocketClassGenerator( final Filer filer ) {
		this.filer = filer;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/websocket-class.mustache" );
	}

	public void generate( final WebSocketData clazz ) throws IOException {
		System.out.println( "Generating WebSocket wrapper for " + clazz.getCanonicalName() );
		final String name = createClassCanonicalName( clazz );
		final JavaFileObject sourceFile = filer.createSourceFile( name );
		final Writer writer = sourceFile.openWriter();
		this.providedClazzTemplate.execute( writer, clazz );
		writer.close();
	}

	String createClassCanonicalName( final WebSocketData clazz ) {
		return String.format( "%s.GeneratedWebSocket%d",
				clazz.getPackageName(),
				clazz.getIdentifier() );
	}
}