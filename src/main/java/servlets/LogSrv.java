package servlets;

import commons.SharedTaskController;
import commons.TaskController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/showLog")
public class LogSrv extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String line;
        PrintWriter pw = resp.getWriter();
        BufferedReader reader = new BufferedReader(new FileReader("logfile.log"));
        try {
            while ((line = reader.readLine()) != null)
                pw.print(line + " <br> ");

        }
        catch (FileNotFoundException ffe) {
            ffe.printStackTrace();
            System.out.println("WEB-> File not found in ''");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("WEB-> IO exception in ''");
        }
        finally {
            reader.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}