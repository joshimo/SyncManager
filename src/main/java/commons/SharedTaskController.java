package commons;

import logger.Logger;
import unitmodel.SyncUnit;

import java.util.*;

public class SharedTaskController implements TaskController, TaskTableController {

    private Vector<Task> taskList = new Vector<>();
    private Vector<TaskRunner> sRunningList = new Vector<>();
    private Vector<TaskRunner> iRunningList = new Vector<>();
    private Vector<Vector<String>> taskTable = new Vector<>();

    private static SharedTaskController ourInstance = new SharedTaskController();

    public static SharedTaskController getInstance() {
        return ourInstance;
    }

    @Override
    public void setLogger(Logger logger) {
        for (Task t : taskList) {
            t.getController().setLogger(logger);
            System.out.println(t);
        }
    }

    private SharedTaskController() {
        System.out.println("Creating shared components");
        taskList = IOController.loadTaskList();
        for (Task t : taskList) {
            t.assignController();
            t.create();
        }

        createRunningList();
        refreshTaskTable();
    }

    private void createRunningList() {
        iRunningList = new Vector<>();
        sRunningList = new Vector<>();

        for (int i = 0; i < taskList.size(); i ++) {
            TaskRunner itr = new TaskRunner();
            TaskRunner str = new TaskRunner();
            itr.setTask(taskList.get(i));
            str.setTask(taskList.get(i));
            sRunningList.add(str);
            iRunningList.add(itr);
        }
    }

    private void refreshTaskTable() {
        taskTable = new Vector<>();

        for (int taskNum = 0; taskNum < taskList.size(); taskNum ++) {

            Task task = taskList.get(taskNum);
            Vector<String> tableRow = new Vector<>();

            tableRow.add(0, task.getSourceDirectory());
            tableRow.add(1, task.getDestinationDirectory());
            tableRow.add(2, task.isDeleteFromDestination() ? "Yes" : "No");
            tableRow.add(3, task.getLastSyncDate());
            tableRow.add(4, task.getTaskStatus());
            tableRow.add(5, task.getScheduleType());
            tableRow.add(6, task.getScheduleRepresentation());
            tableRow.add(7, this.getTaskRunningStatus(taskNum));

            taskTable.add(tableRow);
        }
    }


    /** TaskController methods implementation */
    @Override
    public boolean saveTaskList() {
        return IOController.saveTaskList(taskList);
    }

    @Override
    public void stopTask(int taskNum) {
        this.iRunningList.get(taskNum).stop();
        this.sRunningList.get(taskNum).stop();
        TaskRunner iTaskRunner = new TaskRunner();
        TaskRunner sTaskRunner = new TaskRunner();
        iTaskRunner.setTask(this.taskList.get(taskNum));
        sTaskRunner.setTask(this.taskList.get(taskNum));
        this.iRunningList.set(taskNum, iTaskRunner);
        this.sRunningList.set(taskNum, sTaskRunner);

        this.refreshTaskTable();
    }

    @Override
    public void startTask(int taskNum) {
        Task task = this.taskList.get(taskNum);

        Timer iTimer = new Timer();
        Timer sTimer = new Timer();

        if (!this.iRunningList.get(taskNum).isRunning())
            if (task.isSyncByInterval()) {
                iTimer.schedule(this.iRunningList.get(taskNum), task.getRunningInterval(), task.getRunningInterval());
                this.iRunningList.get(taskNum).setRunning(true);
                System.out.println("II" + taskNum);
            }
        if (!this.sRunningList.get(taskNum).isRunning())
            if (task.isSyncBySchedule()) {
                sTimer.schedule(this.sRunningList.get(taskNum), task.getRunningDate(), day);
                this.sRunningList.get(taskNum).setRunning(true);
                System.out.println("SS" + taskNum);
            }

        this.refreshTaskTable();
    }

    @Override
    public void deleteTask(int taskNum) {
        this.taskList.remove(taskNum);
        this.iRunningList.remove(taskNum);
        this.sRunningList.remove(taskNum);

        this.refreshTaskTable();
    }

    @Override
    public void addNewTask(Task task) {
        TaskRunner itr = new TaskRunner();
        TaskRunner str = new TaskRunner();
        this.taskList.add(task);
        itr.setTask(task);
        str.setTask(task);
        this.iRunningList.add(itr);
        this.sRunningList.add(str);

        this.refreshTaskTable();
    }

    @Override
    public void substituteTask(int taskNum, Task task) {
        TaskRunner itr = new TaskRunner();
        TaskRunner str = new TaskRunner();
        this.taskList.set(taskNum, task);
        itr.setTask(task);
        str.setTask(task);
        iRunningList.set(taskNum, itr);
        sRunningList.set(taskNum, str);

        this.refreshTaskTable();
    }

    @Override
    public void startAll() {

        for (int taskNum = 0; taskNum < taskList.size(); taskNum ++)
            this.startTask(taskNum);

        this.refreshTaskTable();
    }

    @Override
    public void stopAll() {
        for (int taskNum = 0; taskNum < taskList.size(); taskNum ++) {
            iRunningList.get(taskNum).stop();
            iRunningList.get(taskNum).setRunning(false);
            sRunningList.get(taskNum).stop();
            sRunningList.get(taskNum).setRunning(false);
            TaskRunner iTaskRunner = new TaskRunner();
            TaskRunner sTaskRunner = new TaskRunner();
            iTaskRunner.setTask(taskList.get(taskNum));
            sTaskRunner.setTask(taskList.get(taskNum));
            iRunningList.set(taskNum, iTaskRunner);
            sRunningList.set(taskNum, sTaskRunner);
        }

        this.refreshTaskTable();
    }

    @Override
    public void syncAll() {
        for (TaskRunner tr : iRunningList) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    tr.run();
                }
            };
            try {
                t.start();
                t.join();
            }
            catch (InterruptedException ie) {}
        }

        this.refreshTaskTable();
    }

    @Override
    public Task getTask(int taskNum) {
        return this.taskList.get(taskNum);
    }

    @Override
    public Map<String, Vector<SyncUnit>> getDeletedHistoryAsVector(int taskNum) {
        Task task = this.getTask(taskNum);
        return task.getDeletedHistory();
    }

    @Override
    public Map<String, Vector<SyncUnit>> getModifiedHistoryAsVector(int taskNum) {
        Task task = this.getTask(taskNum);
        return task.getModifiedHistory();
    }

    public Map<String, String[][]> getModifiedHistoryAsArray(int taskNum) {
        Task task = this.getTask(taskNum);
        Map<String, Vector<SyncUnit>> historyMap = task.getModifiedHistory();
        Map<String, String[][]> historyMapArray = new HashMap<>();
        Set<String> keySet = historyMap.keySet();

        for (String key : keySet) {
            Vector<SyncUnit> units = historyMap.get(key);
            String[][] currentArray = new String[units.size()][3];
            for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
                SyncUnit unit = units.get(unitNum);
                currentArray[unitNum][0] = unit.isDirectory() ? "Directory" : "File";
                currentArray[unitNum][1] = unit.getName();
                currentArray[unitNum][2] = unit.getAbsolutePath();
            }
            historyMapArray.put(key, currentArray);
        }

        return historyMapArray;
    }

    @Override
    public int getTaskListSize() {
        return taskList.size();
    }

    @Override
    public String getTaskRunningStatus(int taskNum) {
        if (iRunningList.get(taskNum).isRunning() || sRunningList.get(taskNum).isRunning())
            return Status.STATUS_RUNNING;
        else
            return Status.STATUS_STOPPED;
    }

    public void setPropertyChangeObserver(PropertyChangeObserver observer) {
        for (Task t : taskList)
            t.setObserver(observer);
    }

    /** TaskTableController method implementation */
    @Override
    public Vector<Vector<String>> getTaskTable() {
        refreshTaskTable();
        return taskTable;
    }
}