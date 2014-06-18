package io.skullabs.undertow.urouting;

import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class RoutingMethodData {

	final String type;
	final String packageName;
	final String methodName;
	final String methodParams;
	final String returnType;
	final String responseContentType;
	final String httpPath;
	final String httpMethod;
	final Long identifier = System.currentTimeMillis();

}
