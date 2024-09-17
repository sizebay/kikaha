package kikaha.cloud.smart;

import java.io.IOException;

/**
 * Created by miere.teixeira on 12/06/2017.
 */
public class LocalhostMachineIdentification implements MachineIdentification {

    @Override
    public String getLocalAddress() throws IOException {
        return "127.0.0.1";
    }
}
