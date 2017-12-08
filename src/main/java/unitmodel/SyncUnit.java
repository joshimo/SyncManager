package unitmodel;

import java.io.File;

public class SyncUnit extends File {

    private long lastModDate;

    public long getLastModDate() {
        return lastModDate;
    }

    public SyncUnit(File file) {
        this(file.getAbsolutePath());
    }

    public SyncUnit(String fileName) {
        super(fileName);
        this.lastModDate = this.lastModified();
    }
}