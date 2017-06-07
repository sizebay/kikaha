package kikaha.core;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * Created by ronei.gebert on 07/06/2017.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class Tuple<K,V> implements Serializable {
    final K first;
    final V second;

    public static <K,V> Tuple<K,V> empty(){
        return new Tuple<>(null, null);
    }
}
