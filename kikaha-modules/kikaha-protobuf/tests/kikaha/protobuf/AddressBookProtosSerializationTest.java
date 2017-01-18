package kikaha.protobuf;

import com.example.tutorial.AddressBookProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import io.grpc.examples.GreeterOuterClass;
import org.junit.Test;

/**
 * Created by miere.teixeira on 17/01/2017.
 */
public class AddressBookProtosSerializationTest {

    @Test
    public void ensureThatCanSerializeAddressBook(){

    }
}

class GreeterImpl extends GreeterOuterClass.Greeter {

    @Override
    public void sayHello(RpcController controller, GreeterOuterClass.HelloRequest request, RpcCallback<GreeterOuterClass.HelloReply> done) {

    }
}