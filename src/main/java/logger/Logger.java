package logger;

import java.io.Serializable;

public interface Logger extends Serializable {

    void logEvent(String message);

}
