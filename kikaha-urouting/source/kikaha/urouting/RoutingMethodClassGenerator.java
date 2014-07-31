package kikaha.urouting;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

public class RoutingMethodClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate;
	final Filer filer;

	public RoutingMethodClassGenerator( Filer filer ) {
		this.filer = filer;
		this.providedClazzTemplate = this.mustacheFactory.compile( "META-INF/routing-method-class.mustache" );
	}

	public void generate( RoutingMethodData clazz ) throws IOException {
		String methodCanonicalName = clazz.getType() + "." + clazz.getMethodName();
		System.out.println( "Generating class for method " + methodCanonicalName );
		String name = createClassCanonicalName( clazz );
		JavaFileObject sourceFile = filer.createSourceFile( name );
		Writer writer = sourceFile.openWriter();
		this.providedClazzTemplate.execute( writer, clazz );
		writer.close();
	}

	String createClassCanonicalName( RoutingMethodData clazz ) {
		return String.format( "%s.GeneratedRoutingMethod%d",
				clazz.getPackageName(),
				clazz.getIdentifier() );
	}
}
