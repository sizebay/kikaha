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
	final boolean rawObject;

	public long getIdentifier() {
		return hashCode() & 0xffffffffl;
	}

	public String getClassName(){
		return "GeneratedRoutingMethod" + getIdentifier();
	}

	public String getTargetCanonicalClassName(){
		return packageName + "." + targetClass;
	}
}