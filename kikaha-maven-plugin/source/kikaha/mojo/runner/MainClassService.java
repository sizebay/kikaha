package kikaha.mojo.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class MainClassService {

	volatile Process processCurrentlyActive;
	final Thread destroyer;
	final String mainClass;
	final List<String> classpath;
	final List<String> arguments;
	
	public MainClassService( final String mainClass, final List<String> classpath, List<String> arguments ) {
		this.mainClass = mainClass;
		this.classpath = classpath;
		this.arguments = arguments;
		this.destroyer = new ProcessDestroyer();
		Runtime.getRuntime().addShutdownHook( destroyer );
	}

	public void restart() {
		try {
			stop();
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		destroyer.run();
	}

	public Process start() throws IOException {
		val commandLine = createCommandLine();
		val builder = new ProcessBuilder(commandLine);
		builder.redirectErrorStream(true);

		processCurrentlyActive = builder.start();
		printAsynchronously(processCurrentlyActive.getInputStream());
		printAsynchronously(processCurrentlyActive.getErrorStream());

		return processCurrentlyActive;
	}

	List<String> createCommandLine() {
		val commandLine = new ArrayList<String>();
		commandLine.add(getJavaExecutable());
		commandLine.add("-cp");
		commandLine.add(joinClassPath(classpath));
		commandLine.add(mainClass);
		commandLine.addAll(arguments);
		return commandLine;
	}

	String getJavaExecutable() {
		val javaHome = System.getProperty("java.home");
		return javaHome + File.separator + "bin" + File.separator + "java";
	}

	String joinClassPath(List<String> paths) {
		val pathSeparator = System.getProperty("path.separator");
		val buffer = new StringBuilder();
		boolean first = true;
		for (val path : paths) {
			if (!first)
				buffer.append(pathSeparator);
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
			System.out.println( mainClass + " has shutting down!");
		}
	}
}