package kikaha.protobuf;

import static java.lang.String.format;

/**
 * @author: miere.teixeira
 */
public interface AnnotatedMethodData {
	String getPackageName();
	String getTypeName();
	long getIdentifier();

	default String getType(){
		return format("%s.%s", getPackageName(), getTypeName());
	}

	default String getGeneratedClassName(){
		return format("Generated%s%s", getTypeName(), getIdentifier());
	}

	default long createIdentifier() {
		return hashCode() & 0xffffffffl;
	}
}
