package kikaha.urouting.api;

import java.lang.annotation.*;

@Target( { ElementType.PARAMETER } )
@Retention( RetentionPolicy.RUNTIME )
public @interface FormParam {
	String value();
}
