package commons;

import logger.*;
import java.io.*;
import java.util.Vector;

public class IOController {

    private static Logger logger = AppLogger.getInstance();
    private static String path = System.getProperty("user.dir") + File.separator + "SyncManager" + File.separator + "data";

    static {
        if (!new File(path).exists())
            new File(path).mkdirs();
    }

    public static boolean saveTaskTable(Vector<Vector<String>> tList) {
        try {
                FileOutputStream fos = new FileOutputStream(path + File.separator + "tdata.dat");
                ObjectOutputStream stream = new ObjectOutputStream(fos);
                stream.writeObject(tList);
                stream.flush();
                stream.close();
                return true;
            }
            catch (FileNotFoundException e) {
                logger.logEvent(e.toString());
                return false;
            }
            catch (IOException e) {
                logger.logEvent(e.toString());
                return false;
            }
            catch (Exception e) {
                logger.logEvent(e.toString());
                return false;
            }

    }

    public static boolean saveTaskList(Vector<Task> tList) {
        try {
            FileOutputStream fos = new FileOutputStream(path + File.separator + "tasklist.dat");
            ObjectOutputStream stream = new ObjectOutputStream(fos);
            stream.writeObject(tList);
            stream.flush();
            stream.close();
            return true;
        }
        catch (FileNotFoundException e) {
            logger.logEvent(e.toString());
            return false;
        }
        catch (IOException e) {
            logger.logEvent(e.toString());
            return false;
        }
        catch (Exception e) {
            logger.logEvent(e.toString());
            return false;
        }

    }

    public static Vector<Vector<String>> loadTaskTable() {

        Vector<Vector<String>> tList = new Vector<>();

        try {
            FileInputStream fis = new FileInputStream(path + File.separator + "tdata.dat");
            ObjectInputStream stream = new ObjectInputStream(fis);
            tList = (Vector<Vector<String>>) stream.readObject();
            fis.close();
        }
        catch (ClassNotFoundException cnfe) {
            logger.logEvent(cnfe.toString());
        }
        catch (FileNotFoundException fnfe) {
            logger.logEvent(fnfe.toString());
        }
        catch (IOException ioe) {
            logger.logEvent(ioe.toString());
        }

        return tList;
    }

    public static Vector<Task> loadTaskList() {

        Vector<Task> tList = new Vector();

        try {
            FileInputStream fis = new FileInputStream(path + File.separator + "tasklist.dat");
            ObjectInputStream stream = new ObjectInputStream(fis);
            tList = (Vector<Task>) stream.readObject();
            fis.close();
        }
        catch (ClassNotFoundException cnfe) {
            logger.logEvent(cnfe.toString());
        }
        catch (FileNotFoundException fnfe) {
            logger.logEvent(fnfe.toString());
        }
        catch (IOException ioe) {
            logger.logEvent(ioe.toString());
        }

        return tList;
    }
}