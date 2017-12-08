package synccontroller;

import logger.Logger;
import unitmodel.SyncUnit;

import java.util.Map;
import java.util.Vector;

public interface SyncController {

    boolean synchronize();

    void setSourceDirectory(String sourceDirectory);

    void setDestinationDirectory(String sourceDirectory);

    Map<String, Vector<SyncUnit>> getDeletedHistory();

    Map<String, Vector<SyncUnit>> getModifiedHistory();

    boolean check() throws SourceNotFoundException, DestinationNotFoundException;

    void setLogger(Logger logger);

}
