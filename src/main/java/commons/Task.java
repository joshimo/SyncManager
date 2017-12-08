package commons;

import synccontroller.SourceNotFoundException;
import synccontroller.DestinationNotFoundException;
import synccontroller.SyncController;
import synccontroller.SyncUnitController;
import unitmodel.SyncUnit;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

public class Task implements Serializable{

    private String sourceDirectory;
    private String destinationDirectory;
    transient private SyncController controller;
    private String lastSyncDate = "";
    private String taskStatus = Status.NEW_ADDED;

    private String scheduleType;
    private String scheduleRepresentation = "";

    private Long runningInterval;
    private Long firstRunInterval;

    private Date runningDate;

    private String intervalHours;
    private String intervalMinutes;
    private String intervalSeconds;

    private String scheduleHours;
    private String scheduleMinutes;
    private String scheduleSeconds;

    private boolean syncByInterval;
    private boolean syncBySchedule;
    private boolean deleteFromDestination;
    private boolean keepPrevious;

    private int previousCopiesNum;
    private int previousCopiesDays;

    transient private PropertyChangeObserver observer;


    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }


    public String getScheduleType() {
        return scheduleType;
    }

    public String getScheduleRepresentation() {
        return scheduleRepresentation;
    }

    public Long getRunningInterval() {
        return runningInterval;
    }

    public Date getRunningDate() {
        return runningDate;
    }

    public String getIntervalHours() {
        return intervalHours;
    }

    public String getIntervalMinutes() {
        return intervalMinutes;
    }

    public String getIntervalSeconds() {
        return intervalSeconds;
    }

    public String getScheduleHours() {
        return scheduleHours;
    }

    public String getScheduleMinutes() {
        return scheduleMinutes;
    }

    public String getScheduleSeconds() {
        return scheduleSeconds;
    }

    public boolean isSyncByInterval() {
        return syncByInterval;
    }

    public boolean isSyncBySchedule() {
        return syncBySchedule;
    }

    public boolean isDeleteFromDestination() {
        return deleteFromDestination;
    }

    public boolean isKeepPrevious() {
        return keepPrevious;
    }

    public int getPreviousCopiesNum() {
        return previousCopiesNum;
    }

    public int getPreviousCopiesDays() {
        return previousCopiesDays;
    }

    public Map<String, Vector<SyncUnit>> getDeletedHistory() {
        return controller.getDeletedHistory();
    }

    public Map<String, Vector<SyncUnit>> getModifiedHistory() {
        return controller.getModifiedHistory();
    }

    public SyncController getController() {
        return controller;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        controller.setSourceDirectory(sourceDirectory);
    }

    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        controller.setDestinationDirectory(destinationDirectory);
    }

    public Task setIntervalHours(String intervalHours) {
        this.intervalHours = filter(intervalHours);
        return this;
    }

    public Task setIntervalMinutes(String intervalMinutes) {
        this.intervalMinutes = filter(intervalMinutes);
        return this;
    }

    public Task setIntervalSeconds(String intervalSeconds) {
        this.intervalSeconds = filter(intervalSeconds);
        return this;
    }

    public Task setScheduleHours(String scheduleHours) {
        this.scheduleHours = filter(scheduleHours);
        return this;
    }

    public Task setScheduleMinutes(String scheduleMinutes) {
        this.scheduleMinutes = filter(scheduleMinutes);
        return this;
    }

    public Task setScheduleSeconds(String scheduleSeconds) {
        this.scheduleSeconds = filter(scheduleSeconds);
        return this;
    }

    public Task setSyncByInterval(boolean syncByInterval) {
        this.syncByInterval = syncByInterval;
        return this;
    }

    public Task setSyncBySchedule(boolean syncBySchedule) {
        this.syncBySchedule = syncBySchedule;
        return this;
    }

    public Task setDeleteFromDestination(boolean deleteFromDestination) {
        this.deleteFromDestination = deleteFromDestination;
        return this;
    }

    public Task setKeepPrevious(boolean keepPrevious) {
        this.keepPrevious = keepPrevious;
        return this;
    }

    public Task setPreviousCopiesNum(int previousCopiesNum) {
        this.previousCopiesNum = previousCopiesNum;
        return this;
    }

    public Task setPreviousCopiesDays(int previousCopiesDays) {
        this.previousCopiesDays = previousCopiesDays;
        return this;
    }


    public void setObserver(PropertyChangeObserver observer) {
        this.observer = observer;
    }


    public Task() {
    }

    public Task(String sourceDirectory, String destinationDirectory) {
        this(sourceDirectory, destinationDirectory, "");
    }

    public Task (String sourceDirectory, String destinationDirectory, String lastSyncDate) {

        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        this.lastSyncDate = lastSyncDate;

        this.assignController();
    }

    public void assignController() {
        try {
            controller = new SyncUnitController(this.sourceDirectory, this.destinationDirectory, this.deleteFromDestination);

            if (controller.check())
                taskStatus = Status.UP_TO_DATE;
            else
            if (getLastSyncDate() == null)
                taskStatus = Status.NEW_ADDED;
            else
                taskStatus = Status.OLD;
        }
        catch (SourceNotFoundException snfe) {
            System.out.println("source not exist");
            taskStatus = Status.S_NOT_EXIST;
        }
        catch (DestinationNotFoundException dnfe) {
            System.out.println("destination not exist");
            taskStatus = Status.D_NOT_EXIST;
        }
    }

    public void run() {
        if (controller.synchronize()) {
            this.lastSyncDate = new Date().toString();
            this.taskStatus = Status.UP_TO_DATE;
        }
        else
            this.taskStatus = Status.SYNC_ERROR;
        observer.changed();
    }

    @SuppressWarnings("deprecation")
    public Task create() {

        if (isSyncByInterval()) {
            runningInterval = parse(intervalHours, intervalMinutes, intervalSeconds);
            scheduleType = Status.RUN_BY_TIMER;
            scheduleRepresentation = "i: " + intervalHours + ":" + intervalMinutes + ":" + intervalSeconds;
        }

        if (isSyncBySchedule()) {
            firstRunInterval = parse(scheduleHours, scheduleMinutes, scheduleSeconds);
            Date today = new Date(new Date().getYear(), new Date().getMonth(), new Date().getDate());
            if ((today.getTime() + firstRunInterval) > new Date().getTime())
                runningDate = new Date(today.getTime() + firstRunInterval);
            else
                runningDate = new Date(new Date(new Date().getYear(), new Date().getMonth(), new Date().getDate() + 1).getTime() + firstRunInterval);
            scheduleType = Status.RUN_BY_SCHEDULE;
            scheduleRepresentation = "s: " + scheduleHours + ":" + scheduleMinutes + ":" + scheduleSeconds;
        }

        if (isSyncBySchedule() && isSyncByInterval()) {
            scheduleType = Status.RUN_BY_BOTH;
            scheduleRepresentation = "i: " + intervalHours + ":" + intervalMinutes + ":" + intervalSeconds + "\n";
            scheduleRepresentation += "s: " + scheduleHours + ":" + scheduleMinutes + ":" + scheduleSeconds;
        }

        return this;
    }

    private String filter(String s) {

        if (s == null) s = "";

        char[] sequence = s.toCharArray();
        String str = "";

        if (sequence.length > 0) {
            for (char c : sequence)
                if ((c >= 48) && (c <= 59))
                    str += c;

            if (str.length() == 0) str = "00";
            if (str.length() == 1) str = "0" + str;
        }

        return str;
    }

    private Long parse(String hh, String mm, String ss) {

        if (hh == null || hh.isEmpty()) hh = "00";
        if (mm == null || mm.isEmpty()) mm = "00";
        if (ss == null || ss.isEmpty()) ss = "00";

        hh = filter(hh);
        mm = filter(mm);
        ss = filter(ss);

        return Long.parseLong(hh) * 3600 * 1000 + Long.parseLong(mm) * 60 * 1000 + Long.parseLong(ss) * 1000;
    }

    @Override
    public String toString() {
        return  "\nSource directory:" + this.getSourceDirectory() +
                "\nDestination directory:" + this.getDestinationDirectory() +
                "\nTask status:" + this.getTaskStatus() +
                "\nDelete from destination:" + this.isDeleteFromDestination() +
                "\nSchedule: \n" + this.getScheduleRepresentation() +
                "\nSchedule type: " + this.getScheduleType() +
                "\nDelete from destination: " + this.isDeleteFromDestination() +
                "\nKeep previous copies: " + this.isKeepPrevious() +
                "\nKeep last copies number: " + this.getPreviousCopiesNum() +
                "\nKeep last copies during (days): " + this.getPreviousCopiesDays() +
                "\nRunning interval (ms): " + this.getRunningInterval() +
                "\nNext running date: " + this.getRunningDate();
    }
}