package kikaha.protobuf;

import java.lang.annotation.*;
import io.undertow.util.Methods;

/**
 * @author: miere.teixeira
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.SOURCE )
public @interface RPC {
}
