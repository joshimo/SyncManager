package logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class WebLogger implements Logger {

    private static Logger ourInstance = new WebLogger();

    public static Logger getInstance() {
        return ourInstance;
    }

    private WebLogger() {
    }

    @Override
    public void logEvent(String message) {
        System.out.println("WEB-> " + message);
        logToFile("WEB-> " + message + "\n");
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
            System.out.println("WEB-> File not found in 'void logEvent(String str)'");
        }
        catch (IOException ioe) {
            System.out.println("WEB-> IO exception in 'void logEvent(String str)'");
        }
    }

}
