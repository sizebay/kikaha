package io.skullabs.undertow.hazelcast;

import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD } )
public @interface Source {
	String value();
}
