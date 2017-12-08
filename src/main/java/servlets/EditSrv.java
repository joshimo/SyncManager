package servlets;

import commons.SharedTaskController;
import commons.Task;
import commons.TaskController;
import logger.Logger;
import logger.WebLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/edit")
public class EditSrv extends HttpServlet {

    private int taskNum;

    private TaskController taskController = SharedTaskController.getInstance();
    private Logger logger = WebLogger.getInstance();
    private Task task;

    {
        taskController.setLogger(logger);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (!(req.getParameter("taskNumber").isEmpty() || req.getParameter("taskNumber") == null))
            this.taskNum = Integer.parseInt(req.getParameter("taskNumber"));
        else
            this.taskNum = -1;

        task = taskController.getTask(taskNum);

        req.setAttribute("sourceDirectory", task.getSourceDirectory());
        req.setAttribute("destinationDirectory", task.getDestinationDirectory());

        if (task.isDeleteFromDestination())
            req.setAttribute("deleteFromDestination", "checked");
        else
            req.setAttribute("deleteFromDestination", "");

        if (task.isKeepPrevious())
            req.setAttribute("keepPrev", "checked");
        else
            req.setAttribute("keepPrev", "");

        req.setAttribute("prevNum", task.getPreviousCopiesNum());
        req.setAttribute("prevDays", task.getPreviousCopiesDays());

        if (task.isSyncByInterval())
            req.setAttribute("syncByInterval", "checked");
        else
            req.setAttribute("syncByInterval", "");

        req.setAttribute("intervalHH", task.getIntervalHours());
        req.setAttribute("intervalMM", task.getIntervalMinutes());
        req.setAttribute("intervalSS", task.getIntervalSeconds());

        if(task.isSyncBySchedule())
            req.setAttribute("syncBySchedule", "checked");
        else
            req.setAttribute("syncBySchedule", "");

        req.setAttribute("scheduleHH", task.getScheduleHours());
        req.setAttribute("scheduleMM", task.getScheduleMinutes());
        req.setAttribute("scheduleSS", task.getScheduleSeconds());

        req.getRequestDispatcher("editPage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Map<String, String[]> args = req.getParameterMap();

        editTask(args);

        req.getRequestDispatcher("/sync").forward(req, resp);
    }

    private void editTask(Map<String, String[]> args) {

        String sourceDirectory = "";
        String destinationDirectory = "";

        String intervalHH = "";
        String intervalMM = "";
        String intervalSS = "";

        String scheduleHH = "";
        String scheduleMM = "";
        String scheduleSS = "";

        boolean syncByInterval;
        boolean syncBySchedule;
        boolean deleteFromDestination;
        boolean keepPrev;

        int prevNum;
        int prevDays;

        if (!args.get("sourceDirectory")[0].isEmpty())
            sourceDirectory = args.get("sourceDirectory")[0];

        if (!args.get("destinationDirectory")[0].isEmpty())
            destinationDirectory = args.get("destinationDirectory")[0];

        try {
            if (args.get("deleteFromDestination")[0].equals("true"))
                deleteFromDestination = true;
            else
                deleteFromDestination = false;
        }
        catch (NullPointerException npe) {
            deleteFromDestination = false;
        }

        try {
            keepPrev = args.get("keepPrev")[0].equals("true");
        }
        catch (NullPointerException npe) {
            keepPrev = false;
        }

        try {
            if (args.get("syncBySchedule")[0].equals("true"))
                syncBySchedule = true;
            else
                syncBySchedule = false;
        }
        catch (NullPointerException npe) {
            syncBySchedule = false;
        }

        try {
            if (args.get("syncByInterval")[0].equals("true"))
                syncByInterval = true;
            else
                syncByInterval = false;
        }
        catch (NullPointerException npe) {
            syncByInterval = false;
        }

        if (syncByInterval) {
            try {
                intervalHH = args.get("intervalHours")[0];
            }
            catch (NullPointerException npe) {
                intervalHH = "00";
            }
            try {
                intervalMM = args.get("intervalMinutes")[0];
            }
            catch (NullPointerException npe) {
                intervalMM = "00";
            }
            try {
                intervalSS = args.get("intervalSeconds")[0];
            }
            catch (NullPointerException npe) {
                intervalSS = "00";
            }
        }

        if (syncBySchedule) {
            try {
                scheduleHH = args.get("scheduleHours")[0];
            }
            catch (NullPointerException npe) {
                scheduleHH = "00";
            }
            try {
                scheduleMM = args.get("scheduleMinutes")[0];
            }
            catch (NullPointerException npe) {
                scheduleMM = "00";
            }
            try {
                scheduleSS = args.get("scheduleSeconds")[0];
            }
            catch (NullPointerException npe) {
                scheduleSS = "00";
            }
        }

        try {
            prevNum = Integer.parseInt(args.get("prevNum")[0]);
        }
        catch (NullPointerException npe) {
            prevNum = 0;
        }
        catch (NumberFormatException npe) {
            prevNum = 0;
        }

        try {
            prevDays = Integer.parseInt(args.get("prevDays")[0]);
        }
        catch (NullPointerException npe) {
            prevDays = 0;
        }
        catch (NumberFormatException npe) {
            prevDays = 0;
        }

        this.task.setSourceDirectory(sourceDirectory);
        this.task.setDestinationDirectory(destinationDirectory);
        this.task.setDeleteFromDestination(deleteFromDestination);
        this.task.setSyncByInterval(syncByInterval).setIntervalHours(intervalHH).setIntervalMinutes(intervalMM).setIntervalSeconds(intervalSS);
        this.task.setSyncBySchedule(syncBySchedule).setScheduleHours(scheduleHH).setScheduleMinutes(scheduleMM).setScheduleSeconds(scheduleSS);
        this.task.getController().setLogger(logger);
        this.task.setKeepPrevious(keepPrev).setPreviousCopiesNum(prevNum).setPreviousCopiesDays(prevDays);
        this.task.create();

        taskController.substituteTask(taskNum, this.task);
    }
}