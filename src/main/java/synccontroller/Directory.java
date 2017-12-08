package synccontroller;

import unitmodel.SyncUnit;

import java.io.File;
import java.util.Vector;

public class Directory {

    private static Vector<String> directoryList;
    private static Vector<SyncUnit> directoryUnitsList;

    public static Vector<String> getList(String dir) {

        directoryList = new Vector<>();
        directoryUnitsList = new Vector<>();
        createList(dir);
        return directoryList;
    }


    public static Vector<SyncUnit> getUnitsList(File dir) {

        directoryList = new Vector<>();
        directoryUnitsList = new Vector<>();
        createList(dir.getAbsolutePath());
        return directoryUnitsList;
    }

    public static Vector<String> getList(String dir, String excludeDir) {

        directoryList = new Vector<>();
        createList(dir, excludeDir);
        return directoryList;
    }

    private static void createList(String syncSourceDir, String excludeDir) {
        createList(new File(syncSourceDir), new File(excludeDir));
    }

    private static void createList(String syncSourceDir) {
        createList(new File(syncSourceDir), null);
    }

    private static void createList(File dir, File excludeDir) {

        File[] fileList = dir.listFiles();

        for (File f : fileList) {

            if (excludeDir != null)
                if (!f.getAbsolutePath().equals(excludeDir.getAbsolutePath())) {

                    directoryList.add(f.getAbsolutePath());
                    directoryUnitsList.add(new SyncUnit(f));
                    if (f.isDirectory())
                        createList(f, excludeDir);
                }

            if (excludeDir == null) {
                directoryList.add(f.getAbsolutePath());
                directoryUnitsList.add(new SyncUnit(f));
                if (f.isDirectory())
                    createList(f, excludeDir);
            }
        }
    }

    public static boolean deleteTree(File dir) {

        File[] fileList;

        if (dir.exists())
            if (dir.list().length == 0)
                dir.delete();
            else {
                fileList = dir.listFiles();
                for (File f : fileList) {
                    deleteTree(f);
                    dir.delete();
                }
            }
        return !dir.exists();
    }
}