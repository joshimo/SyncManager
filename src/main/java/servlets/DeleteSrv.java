package servlets;

import commons.SharedTaskController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/delete")
public class DeleteSrv extends HttpServlet {

    private SharedTaskController shared = SharedTaskController.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int taskNum;
        Map<String, String[]> args = req.getParameterMap();

        if (args.get("taskNumber")[0] != null) {
            taskNum = Integer.parseInt(args.get("taskNumber")[0]);
            shared.deleteTask(taskNum);
        }

        req.getRequestDispatcher("/sync").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
