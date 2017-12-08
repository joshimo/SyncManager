package logger;

import console.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class AppLogger implements Logger {

    private static Logger ourInstance = new AppLogger();
    private Console console = SwingConsole.getInstance();

    public static Logger getInstance() {
        return ourInstance;
    }

    private AppLogger() {
    }

    @Override
    public void logEvent(String message) {
        message = "SmApp-> " + message;
        System.out.println(message);
        console.setConsoleText(message);
        logToFile(message + "\n");
    }

    public void logEvent(Object obj) {
        logEvent(obj.toString());
    }

    void logToFile(String str) {

        try {
            FileWriter fw = new FileWriter("logfile.log", true);
            fw.write(str);
            fw.close();
        }
        catch (FileNotFoundException ffe) {
            ffe.printStackTrace();
            System.out.println("App-> File not found in 'void logEvent(String str)'");
        }
        catch (IOException ioe) {
            System.out.println("App-> IO exception in 'void logEvent(String str)'");
        }
    }

}
