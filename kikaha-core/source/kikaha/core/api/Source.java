package kikaha.core.api;

import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD } )
public @interface Source {
	String value();
}
