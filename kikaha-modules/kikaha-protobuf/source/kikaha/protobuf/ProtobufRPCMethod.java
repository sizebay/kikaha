package kikaha.protobuf;

import kikaha.apt.GenerableClass;
import lombok.*;

/**
 * @author: miere.teixeira
 */
@Value
@EqualsAndHashCode( exclude = "identifier" )
public class ProtobufRPCMethod implements GenerableClass {

	String packageName;
	String typeName;
	String methodName;
	String methodParams;
	String returnType;

	String httpPath;
	String httpMethod;

	boolean asyncMode;
	boolean requiresBodyData;

	@Getter( lazy = true )
	private final long identifier = createIdentifier();

	public String toString(){
		return getType() + "." + getMethodName();
	}
}
