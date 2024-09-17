package kikaha.apt;

import static java.lang.String.format;

/**
 * Represents the meta information needed to generate the java class source code.
 */
public interface GenerableClass {

	default String getGeneratedClassCanonicalName() {
		return getPackageName() + "." + getGeneratedClassName();
	}

	default String getGeneratedClassName(){
		return format("Generated%s%s", getTypeName(), getIdentifier());
	}

	default String getType(){
		return format("%s.%s", getPackageName(), getTypeName());
	}

	String getPackageName();

	String getTypeName();

	default long getIdentifier() {
		return createIdentifier();
	}

	default long createIdentifier() {
		return hashCode() & 0xffffffffl;
	}
}
