package servlets;

import commons.*;
import logger.Logger;
import logger.WebLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/new")
public class NewSrv extends HttpServlet {

    private TaskController taskController = SharedTaskController.getInstance();
    private Logger logger = WebLogger.getInstance();

    {
        taskController.setLogger(logger);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("newPage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Map<String, String[]> args;

        try {
            args = req.getParameterMap();
            createNewTask(args);
        }
        catch (NullPointerException npe) {
           doGet(req, resp);
        }

        req.getRequestDispatcher("/sync").forward(req, resp);
    }

    private void createNewTask(Map<String, String[]> args) {

        Task newTask;

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

        int prevNum = 0;
        int prevDays = 0;

        if (!args.get("sourceDirectory")[0].isEmpty())
            sourceDirectory = args.get("sourceDirectory")[0];

        if (!args.get("destinationDirectory")[0].isEmpty())
            destinationDirectory = args.get("destinationDirectory")[0];

        try {
            deleteFromDestination = args.get("deleteFromDestination")[0].equals("true");
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
            syncBySchedule = args.get("syncBySchedule")[0].equals("true");
        }
        catch (NullPointerException npe) {
            syncBySchedule = false;
        }

        try {
            syncByInterval = args.get("syncByInterval")[0].equals("true");
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

        if (keepPrev) {
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
        }

        newTask = new Task(sourceDirectory, destinationDirectory);
        newTask.setDeleteFromDestination(deleteFromDestination);
        newTask.setSyncByInterval(syncByInterval).setIntervalHours(intervalHH).setIntervalMinutes(intervalMM).setIntervalSeconds(intervalSS);
        newTask.setSyncBySchedule(syncBySchedule).setScheduleHours(scheduleHH).setScheduleMinutes(scheduleMM).setScheduleSeconds(scheduleSS);
        newTask.getController().setLogger(logger);
        newTask.setKeepPrevious(keepPrev).setPreviousCopiesNum(prevNum).setPreviousCopiesDays(prevDays);
        newTask.create();

        taskController.addNewTask(newTask);
    }
}