package kikaha.core.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface WebResource {

	String value();
	String method() default "GET";

}
