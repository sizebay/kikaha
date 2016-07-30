package kikaha.mojo.generator;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

final public class APTResult {

	public final boolean success;
	public final List<Diagnostic<? extends JavaFileObject>> diagnostics;

	public APTResult( boolean success, List<Diagnostic<? extends JavaFileObject>> diagnostics ) {
		this.success = success;
		this.diagnostics = diagnostics;
	}
}
