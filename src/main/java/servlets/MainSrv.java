package servlets;

import commons.*;
import logger.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/sync")
public class MainSrv extends HttpServlet implements PropertyChangeObserver {

    private TaskController taskController = SharedTaskController.getInstance();
    private TaskTableController tableController = SharedTaskController.getInstance();
    private Logger logger = WebLogger.getInstance();

    {
        logger.logEvent("\nStarting web application...\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        taskController.setLogger(logger);
        taskController.setPropertyChangeObserver(this);
        redirect(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    public void changed() {
        logger.logEvent("Saving data...");

        if (taskController.saveTaskList())
            logger.logEvent("Data saved");
        else
            logger.logEvent("Error saving data");

    }

    private void redirect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> args = request.getParameterMap();
        String action;
        String dispatcher = "index.jsp";

        if (args.get("btn") != null) {

            action = args.get("btn")[0];
            request.setAttribute("button_action", action);

            if (action.equals("Start All Tasks"))
                taskController.startAll();
            if (action.equals("Stop All Tasks"))
                taskController.stopAll();
            if (action.equals("Sync All Now"))
                taskController.syncAll();
            if (action.equals("Add New Task"))
                dispatcher = "/new";
            if (action.equals("Show log"))
                dispatcher = "/showLog";
            if (action.equals("Regresh Page"))
                dispatcher = "/sync";
        }

        request.setAttribute("date", "Today is " + new Date().toString());
        request.setAttribute("table", tableController.getTaskTable());
        request.getRequestDispatcher(dispatcher).forward(request, response);
    }

    @Override
    public void destroy() {

        taskController.stopAll();
        logger.logEvent("Saving data...");
        if (taskController.saveTaskList())
            logger.logEvent("Data saved");
        else
            logger.logEvent("Error saving data");

        super.destroy();
    }
}