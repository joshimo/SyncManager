package commons;

import logger.Logger;
import unitmodel.SyncUnit;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

public interface TaskController extends Serializable {

    long day = 24 * 3600 * 1000;

    void startAll();

    void stopAll();

    void syncAll();

    void startTask(int taskNum);

    void stopTask(int taskNum);

    void deleteTask(int taskNum);

    void addNewTask(Task task);

    void substituteTask(int taskNum, Task task);

    Task getTask(int taskNum);

    Map<String, Vector<SyncUnit>> getDeletedHistoryAsVector(int taskNum);

    Map<String, Vector<SyncUnit>> getModifiedHistoryAsVector(int taskNum);

    int getTaskListSize();

    String getTaskRunningStatus(int taskNum);

    boolean saveTaskList();

    void setLogger(Logger logger);

    void setPropertyChangeObserver(PropertyChangeObserver observer);
}
