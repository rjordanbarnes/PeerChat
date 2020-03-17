package Discovery;

import java.io.Serializable;

public class RendezvousMessage implements Serializable {
    public String method;
    public Object parameter;

    public RendezvousMessage(String method, Object parameter) {
        this.method = method;
        this.parameter = parameter;
    }
}
