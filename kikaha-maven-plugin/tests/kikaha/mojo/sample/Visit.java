package kikaha.mojo.sample;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )
@Target( { METHOD, TYPE, FIELD } )
public @interface Visit {

}
