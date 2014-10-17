package kikaha.urouting;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import kikaha.urouting.api.WebSocket;

@SupportedAnnotationTypes( "kikaha.urouting.api.*" )
public class MicroWebSocketAnnotationProcessor extends AbstractProcessor {

	WebSocketClassGenerator generator;

	@Override
	public synchronized void init( final ProcessingEnvironment processingEnv ) {
		super.init( processingEnv );
		generator = new WebSocketClassGenerator( filer() );
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment round ) {
		try {
			processClassesAnnotatedWithWebSocket( round );
			return false;
		} catch ( final IOException e ) {
			throw new IllegalStateException( e );
		}
	}

	void processClassesAnnotatedWithWebSocket( final RoundEnvironment round ) throws IOException {
		final Set<? extends Element> elements = round.getElementsAnnotatedWith( WebSocket.class );
		for ( final Element element : elements ) {
			final WebSocketData data = WebSocketData.from( (TypeElement)element );
			generator.generate( data );
		}
	}

	Filer filer() {
		return this.processingEnv.getFiler();
	}

	/**
	 * We just return the latest version of whatever JDK we run on. Stupid?
	 * Yeah, but it's either that or warnings on all versions but 1. Blame Joe.
	 *
	 * PS: this method was copied from Project Lombok. ;)
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.values()[SourceVersion.values().length - 1];
	}
}
