package commons;

import java.util.TimerTask;

public class TaskRunner extends TimerTask {

    private Task task;
    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        this.task.run();
    }

    public void stop() {
        this.cancel();
    }
}