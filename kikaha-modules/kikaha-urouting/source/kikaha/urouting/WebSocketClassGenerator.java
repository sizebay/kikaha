package kikaha.urouting;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.*;
import javax.tools.*;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class WebSocketClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;
	final ProcessingEnvironment processingEnv;

	public WebSocketClassGenerator( final Filer filer, final ProcessingEnvironment processingEnv ) {
		this.filer = filer;
		this.processingEnv = processingEnv;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/websocket-class.mustache" );
	}

	public void generate( final WebSocketData clazz ) throws IOException {
		info( "Generating WebSocket wrapper for " + clazz.getCanonicalName() );
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

	private void info( final String msg ) {
		processingEnv.getMessager().printMessage( Diagnostic.Kind.NOTE, msg );
	}
}