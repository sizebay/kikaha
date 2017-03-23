package kikaha.uworkers.apt;

import kikaha.apt.GenerableClass;
import lombok.*;

/**
 *
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class MicroWorkerListenerClass implements GenerableClass {

	final String packageName;
	final String typeName;
	final String methodName;

	final String parameterType;
	final String endpointURL;
	final boolean rawObject;
}