package servlets;

import commons.SharedTaskController;
import commons.TaskController;
import logger.Logger;
import logger.WebLogger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/stop")
public class StopSrv extends HttpServlet{

    private TaskController taskController = SharedTaskController.getInstance();
    private Logger logger = WebLogger.getInstance();

    {
        taskController.setLogger(logger);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int taskNum;
        Map<String, String[]> args = req.getParameterMap();

        if (args.get("taskNumber")[0] != null) {
            taskNum = Integer.parseInt(args.get("taskNumber")[0]);
            taskController.stopTask(taskNum);
        }

        req.getRequestDispatcher("/sync").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}