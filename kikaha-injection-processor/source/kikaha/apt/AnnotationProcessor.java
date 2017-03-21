package kikaha.apt;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic.Kind;

/**
 * @author: miere.teixeira
 */
public abstract class AnnotationProcessor extends AbstractProcessor {

	protected void info( final String msg ) {
		processingEnv.getMessager().printMessage( Kind.NOTE, msg );
	}

	protected void debug( final String msg ) {
		processingEnv.getMessager().printMessage( Kind.OTHER, msg );
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
