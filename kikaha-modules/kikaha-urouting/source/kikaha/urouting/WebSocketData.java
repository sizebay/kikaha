package kikaha.urouting;

import javax.lang.model.element.ExecutableElement;
import kikaha.apt.GenerableClass;
import lombok.*;

@Getter
@EqualsAndHashCode( exclude = "identifier" )
@RequiredArgsConstructor
public class WebSocketData implements GenerableClass {

	final String packageName;
	final String typeName;

	final String httpPath;
	final String serviceInterface;

	final WebSocketMethodData onOpenMethod;
	final WebSocketMethodData onTextMethod;
	final WebSocketMethodData onCloseMethod;
	final WebSocketMethodData onErrorMethod;

	@Getter( lazy = true )
	private final long identifier = createIdentifier();

	@Override
	public String toString() {
		return getHttpPath() + ":" + getType();
	}
}

@RequiredArgsConstructor
class WebSocketMethodData {

	final String name;
	final String parameters;
	final String returnType;
}