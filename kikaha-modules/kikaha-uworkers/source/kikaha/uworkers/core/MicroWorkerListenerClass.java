package kikaha.uworkers.core;

import lombok.*;

/**
 *
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class MicroWorkerListenerClass {

	final String packageName;
	final String targetClass;
	final String methodName;
	final String parameterType;
	final String endpointName;
	final String endpointURL;
	final boolean rawObject;

	public long getIdentifier() {
		return hashCode() & 0xffffffffl;
	}

	public String getClassName(){
		return "GeneratedWorkerMethod" + getIdentifier();
	}

	public String getTargetCanonicalClassName(){
		return packageName + "." + targetClass;
	}

	public String toString(){
		return packageName + "." + getClassName();
	}
}