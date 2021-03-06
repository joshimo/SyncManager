package synccontroller;

import logger.Logger;
import unitmodel.*;
import logger.*;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Date;

public class SyncUnitController implements SyncController {

    private String sourceDirectory;
    private String destinationDirectory;

    private String deletedDirectory;
    private String modifiedDirectory;
    private String historyDirectory;

    private boolean deleteFromDestination;

    transient private Logger logger;

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    private SyncUnitController() {
    }

    public SyncUnitController(String sourceDirectoryName, String destinationDirectoryName, boolean deleteFromDestination) throws SourceNotFoundException, DestinationNotFoundException {

        this();

        this.setSourceDirectory(sourceDirectoryName);
        this.setDestinationDirectory(destinationDirectoryName);
        this.deleteFromDestination = deleteFromDestination;

        this.historyDirectory = destinationDirectory + File.separator + "_HISTORY_" + new File(destinationDirectory).getName();
        this.deletedDirectory = historyDirectory + File.separator + "DELETED" + File.separator;
        this.modifiedDirectory = historyDirectory + File.separator + "MODIFIED" + File.separator;

        if (logger != null)
            logger.logEvent("Starting task " + sourceDirectoryName + " at " + new Date().toString() + ", delete = " + this.deleteFromDestination);
    }

    private boolean compare(String sourceUnitName, String destinationUnitName) {
        return compare(new SyncUnit(sourceUnitName), new SyncUnit(destinationUnitName));
    }

    private boolean compare(SyncUnit sourceUnit, SyncUnit destinationUnit) {
        return sourceUnit.getLastModDate() > destinationUnit.getLastModDate();
    }

    private Vector<SyncUnit> createSyncUnitList() throws SourceNotFoundException, DestinationNotFoundException {

        Vector<SyncUnit> syncUnitList = new Vector<>();
        Vector<String> sourceDirectoryList;

        checkSourceAndDestinationFolders();

        sourceDirectoryList = Directory.getList(sourceDirectory);

        for (String currentElementName : sourceDirectoryList)
            if (! new File(currentElementName.replace(sourceDirectory, destinationDirectory)).exists())
                syncUnitList.add(new SyncUnit(currentElementName));
            else
                if (compare(currentElementName, currentElementName.replace(sourceDirectory, destinationDirectory)))
                    syncUnitList.add(new SyncUnit(currentElementName));

        return syncUnitList;
    }

    private Vector<SyncUnit> createKeepUnitList() throws SourceNotFoundException, DestinationNotFoundException {

        Vector<SyncUnit> keepUnitList = new Vector<>();
        Vector<String> sourceDirectoryList;

        checkSourceAndDestinationFolders();

        sourceDirectoryList = Directory.getList(sourceDirectory);

        for (String currentElementName : sourceDirectoryList)
            if (new File(currentElementName.replace(sourceDirectory, destinationDirectory)).exists())
                if (compare(currentElementName, currentElementName.replace(sourceDirectory, destinationDirectory)))
                    keepUnitList.add(new SyncUnit(currentElementName.replace(sourceDirectory, destinationDirectory)));

        return keepUnitList;
    }

    @Override
    public boolean check() throws SourceNotFoundException, DestinationNotFoundException {

        //syncUnitList = createSyncUnitList();
        return !(createSyncUnitList().size() > 0);
    }

    private Vector<SyncUnit> createDeleteList() throws SourceNotFoundException, DestinationNotFoundException {

        Vector<SyncUnit> deleteUnitList = new Vector<>();
        Vector<String> destinationDirectoryList;

        checkSourceAndDestinationFolders();

        destinationDirectoryList = Directory.getList(destinationDirectory, historyDirectory);

        for (String currentElementName : destinationDirectoryList)
            if (! new File(currentElementName.replace(destinationDirectory, sourceDirectory)).exists())
                deleteUnitList.add(new SyncUnit(currentElementName));

        return deleteUnitList;
    }

    @Override
    public synchronized boolean synchronize() {

        boolean result = true;

        try {

            Vector<SyncUnit> syncUnitList = createSyncUnitList();
            Vector<SyncUnit> keepUnitList = createKeepUnitList();
            Vector<SyncUnit> deleteUnitList = createDeleteList();

            moveFilesToModifiedHistoryDir(keepUnitList);

            for (SyncUnit unit : syncUnitList) {
                if (unit.isDirectory() && !new File(unit.getAbsolutePath().replace(sourceDirectory, destinationDirectory)).exists())
                    if (new File(unit.getAbsolutePath().replace(sourceDirectory, destinationDirectory)).mkdirs())
                        logger.logEvent(unit.getAbsolutePath() + " ---> OK");
                    else {
                        logger.logEvent(unit.getAbsolutePath() + " ---> ERROR!");
                        result = false;
                    }
            }

            for (SyncUnit unit : syncUnitList) {
                if (unit.isFile())
                    if (copyFile(unit, new File(unit.getAbsolutePath().replace(sourceDirectory, destinationDirectory))))
                        logger.logEvent(unit.getAbsolutePath() + " ---> OK");
                    else {
                        logger.logEvent(unit.getAbsolutePath() + " ---> ERROR!");
                        result = false;
                    }
            }

            if (deleteFromDestination) {

                for (SyncUnit unit : deleteUnitList) {
                    if (unit.isFile())
                        if (unit.delete())
                            logger.logEvent(unit.getAbsolutePath() + " ---> DELETED");
                        else {
                            logger.logEvent(unit.getAbsolutePath() + " ---> ERROR!");
                            result = false;
                        }
                }

                for (SyncUnit unit : deleteUnitList) {
                    if (unit.isDirectory())
                        if (unit.listFiles().length == 0) {
                            if (unit.delete())
                                logger.logEvent(unit.getAbsolutePath() + " ---> DIRECTORY DELETED");
                            else {
                                logger.logEvent(unit.getAbsolutePath() + " ---> ERROR! DIRECTORY WAS NOT DELETED!");
                                result = false;
                            }
                        }
                }
            }

            else

                moveFilesToDeletedHistoryDir(deleteUnitList);

        }
        catch (NullPointerException npe) {
            logger.logEvent("Null pointer exception in 'synchronize()'\n" + npe.getMessage());
            result = false;
        }
        catch (SourceNotFoundException snfe) {
            logger.logEvent("Source directory not found!\n" + snfe.getMessage());
            result = false;
        }
        catch (DestinationNotFoundException dnfe) {
            logger.logEvent("Destination directory not found!\n" + dnfe.getMessage());
            result = false;
        }
        catch (Exception e) {
            logger.logEvent("Exception in 'synchronize()'\n" + e.getMessage());
            result = false;
        }
        finally {
            if (result) logger.logEvent("Synchronization of " + sourceDirectory + " completed successfully at " + new Date().toString());
            else logger.logEvent("Synchronization of " + sourceDirectory + " was not completed!");
            return result;
        }
    }

    @Override
    public Map<String, Vector<SyncUnit>> getDeletedHistory() {
        return getHistory(deletedDirectory);
    }

    @Override
    public Map<String, Vector<SyncUnit>> getModifiedHistory() {
        return getHistory(modifiedDirectory);
    }

    private Map<String, Vector<SyncUnit>> getHistory(String dir) {
        Map<String, Vector<SyncUnit>> history = new HashMap<>();

        File[] fList = new File(dir).listFiles();
        String[] sList = new File(dir).list();

        if (fList != null && sList != null)
            for (int num = 0; num < fList.length; num ++)
                history.put(new Date(Long.parseLong(sList[num])).toString(), Directory.getUnitsList(fList[num]));

        return history;
    }

    private void moveFilesToDeletedHistoryDir(Vector<SyncUnit> deleteUnitList) throws SourceNotFoundException, DestinationNotFoundException{

        Date date = new Date();
        String oldDirectoryPath = deletedDirectory + File.separator + date.getTime();

        File oldDirectory = new File(oldDirectoryPath);

        if (!oldDirectory.exists() && deleteUnitList.size() > 0)
            oldDirectory.mkdirs();

        for (SyncUnit unit : deleteUnitList) {
            if (unit.isDirectory())
                if (new File(unit.getAbsolutePath().replace(destinationDirectory, oldDirectoryPath + File.separator)).mkdirs())
                    logger.logEvent(unit.getAbsolutePath() + " ---> moved to DELETED directory");
        }

        for (SyncUnit unit : deleteUnitList) {
            if (unit.isFile()) {

                File destination = new File(unit.getAbsolutePath().replace(destinationDirectory, oldDirectoryPath + File.separator));
                if (!destination.getParentFile().exists()) destination.getParentFile().mkdirs();

                if (copyFile(unit, new File(unit.getAbsolutePath().replace(destinationDirectory, oldDirectoryPath + File.separator))))
                    logger.logEvent(unit.getAbsolutePath() + " ---> moved to DELETED directory");
                if (unit.delete())
                    logger.logEvent(unit.getAbsolutePath() + " ---> DELETED");
                else
                    logger.logEvent(unit.getAbsolutePath() + " ---> ERROR!");
            }
        }
        for (SyncUnit unit : deleteUnitList) {
            if (unit.isDirectory()) {
                if (Directory.deleteTree(unit))
                    logger.logEvent(unit.getAbsolutePath() + " ---> DIRECTORY DELETED");
                else
                    logger.logEvent(unit.getAbsolutePath() + " ---> ERROR! DIRECTORY WAS NOT DELETED!");
            }
        }
    }

    private void moveFilesToModifiedHistoryDir(Vector<SyncUnit> keepUnitList) throws SourceNotFoundException, DestinationNotFoundException{

        Date date = new Date();
        String modDirectoryPath = modifiedDirectory + File.separator + date.getTime();

        File modDirectory = new File(modDirectoryPath);

        if (keepUnitList.size() > 0) {

            modDirectory.mkdirs();
            System.out.println(keepUnitList);

            for (SyncUnit unit : keepUnitList) {
                if (unit.isDirectory())
                    if (new File(unit.getAbsolutePath().replace(destinationDirectory, modDirectoryPath + File.separator)).mkdirs())
                        logger.logEvent(unit.getAbsolutePath() + " ---> moved to MODIFIED directory");
            }

            for (SyncUnit unit : keepUnitList) {
                if (unit.isFile()) {
                    new File(unit.getAbsolutePath().replace(destinationDirectory, modDirectoryPath + File.separator)).getParentFile().mkdirs();
                    if (copyFile(unit, new File(unit.getAbsolutePath().replace(destinationDirectory, modDirectoryPath + File.separator))))
                        logger.logEvent(unit.getAbsolutePath() + " ---> moved to MODIFIED directory");
                }
            }
        }
    }

    private boolean copyFile(File source, File destination) throws SourceNotFoundException, DestinationNotFoundException {

        boolean isCompleted = false;

        if (checkSourceAndDestinationFolders())
            try (FileOutputStream fos = new FileOutputStream(destination); FileInputStream fis = new FileInputStream(source)) {

                int bufferLength = 8;
                int fileLength = fis.available();
                if (fileLength >= 16777216) bufferLength = 131072;
                if ((fileLength >= 1048576) & (fileLength < 16777216)) bufferLength = 8192;
                if ((fileLength >= 65536) & (fileLength < 1048576)) bufferLength = 4096;
                if ((fileLength >= 4096) & (fileLength < 65536)) bufferLength = 256;
                if ((fileLength >= 1024) & (fileLength < 4096)) bufferLength = 64;

                byte[] buffer = new byte[bufferLength];
                byte[] oneByte = new byte[1];

                while (fis.available() > 0) {
                    if (fis.available() >= bufferLength) {
                        fis.read(buffer);
                        fos.write(buffer);
                    }
                    else {
                        fis.read(oneByte);
                        fos.write(oneByte);
                    }
                }
                fos.close();
                isCompleted = true;
            }
            catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
                logger.logEvent("Cannot copy " + source + "\n" + fnf.toString());
            }
            catch (IOException io) {
                io.printStackTrace();
                logger.logEvent("Cannot copy " + source + "\n" + io.toString());
            }
            finally {
                return isCompleted;
            }
        else
            return isCompleted;
    }

    private boolean checkSourceAndDestinationFolders() throws SourceNotFoundException, DestinationNotFoundException {

        if (sourceDirectory == null || sourceDirectory.isEmpty() || sourceDirectory.length() < 3)
            throw new SourceNotFoundException();
        if (destinationDirectory == null || destinationDirectory.isEmpty() || destinationDirectory.length() < 3)
            throw new DestinationNotFoundException();

        if (!new File(sourceDirectory).exists())
            throw new SourceNotFoundException();
        if (!new File(destinationDirectory).exists())
            throw new DestinationNotFoundException();

        return true;
    }

    public static void main (String... args) throws SourceNotFoundException, DestinationNotFoundException {
        String source = "c:\\syncTest\\!source0\\";
        String destination = "d:\\syncTest\\!destination0\\";
        Logger logger = TestLogger.getInstance();
        boolean deleteFromDestination = false;

        SyncUnitController controller = new SyncUnitController(source, destination, deleteFromDestination);

        //System.out.println("history dir = " + controller.historyDirectory);
        //System.out.println("deleted dir = " + controller.deletedDirectory);
        //System.out.println("modified dir = " + controller.modifiedDirectory);

        controller.setLogger(logger);
        if (controller.synchronize())
            logger.logEvent("Test synchronization finished successfully!");

        /*for (String s : Files.getList(destination))
        System.out.println(s);*/
    }
}