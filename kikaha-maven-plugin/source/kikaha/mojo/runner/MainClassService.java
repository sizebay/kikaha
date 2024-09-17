package kikaha.mojo.runner;

import java.io.*;
import java.util.*;
import lombok.*;
import org.apache.maven.plugin.logging.Log;

@RequiredArgsConstructor
public class MainClassService {

	volatile Process processCurrentlyActive;
	final Thread destroyer;
	final String mainClass;
	final File workingDirectory;
	final List<String> classpath;
	final List<String> arguments;
	final String jvmArgs;
	final Log log;

	public MainClassService( final File workingDirectory, final String mainClass, final List<String> classpath, final List<String> arguments, final String jvmArgs, final Log log ) {
		this.workingDirectory = workingDirectory;
		this.mainClass = mainClass;
		this.classpath = classpath;
		this.arguments = arguments;
		this.destroyer = new ProcessDestroyer();
		this.jvmArgs = jvmArgs;
		this.log = log;
		Runtime.getRuntime().addShutdownHook( destroyer );
	}

	public void restart() {
		try {
			stop();
			start();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		destroyer.run();
	}

	public Process start() throws IOException {
		logImportantInformation();

		val commandLine = createCommandLine();
		System.out.println(commandLine);
		val builder = new ProcessBuilder(commandLine);
		builder.directory( workingDirectory );
		builder.redirectErrorStream(true);

		processCurrentlyActive = builder.start();
		printAsynchronously(processCurrentlyActive.getInputStream());
		printAsynchronously(processCurrentlyActive.getErrorStream());

		return processCurrentlyActive;
	}

	private void logImportantInformation() {
		if ( jvmArgs != null && !jvmArgs.isEmpty() )
			log.info("JVM options defined on pom: " + jvmArgs);
		val runtimeJvmOpts = System.getenv( "KIKAHA_JVM_OPTS" );
		if ( runtimeJvmOpts != null )
			log.debug("Runtime JVM options: " + runtimeJvmOpts);
	}

	List<String> createCommandLine() {
		val commandLine = new ArrayList<String>();
		commandLine.add(getJavaExecutable());
		commandLine.addAll(getJvmArgsAsList());
		commandLine.add("-cp");
		commandLine.add(joinClassPath(classpath));
		commandLine.addAll(arguments);
		commandLine.add(mainClass);
		return commandLine;
	}

	List<String> getJvmArgsAsList(){
		String jvmOpts = jvmArgs;
		val runtimeJvmOpts = System.getenv( "KIKAHA_JVM_OPTS" );
		if ( runtimeJvmOpts != null )
			jvmOpts+= " " + runtimeJvmOpts;

		final List<String> list = new ArrayList<String>();
		for ( final String arg : jvmOpts.split(" ") )
			list.add(arg);
		return list;
	}

	String getJavaExecutable() {
		val javaHome = System.getProperty("java.home");
		return javaHome + File.separator + "bin" + File.separator + "java";
	}

	String joinClassPath(final List<String> paths) {
		val pathSeparator = System.getProperty("path.separator");
		return join( paths, pathSeparator );
	}

	String join( final List<String> strings, final String separator ) {
		val buffer = new StringBuilder();
		boolean first = true;
		for (val path : strings) {
			if (!first)
				buffer.append(separator);
			buffer.append(path);
			first = false;
		}
		return buffer.toString();
	}

	void printAsynchronously(final InputStream stream) {
		new Thread(new ProcessOutputPrinter(stream)).start();
	}

	@RequiredArgsConstructor
	class ProcessDestroyer extends Thread {

		@Override
		public void run() {
			processCurrentlyActive.destroy();
			log.info( mainClass + " has shutting down!");
		}
	}
}