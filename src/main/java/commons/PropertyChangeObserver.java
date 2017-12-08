package commons;

import java.io.Serializable;

public interface PropertyChangeObserver extends Serializable {

    void changed();

}
