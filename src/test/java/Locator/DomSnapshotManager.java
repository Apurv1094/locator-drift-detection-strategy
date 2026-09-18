package Locator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DomSnapshotManager {

    private static final String SNAPSHOT_DIRECTORY =
            "D:\\LocatorHealthPOC\\locator-health-poc\\snapshots";


    public Path getSnapshotPath(String pageName) {

        return Paths.get(
                SNAPSHOT_DIRECTORY,
                pageName + ".html"
        );
    }


    public boolean snapshotExists(String pageName) {

        return Files.exists(
                getSnapshotPath(pageName)
        );
    }


    public void saveSnapshot(
            String pageName,
            String html)
            throws IOException {

        Path snapshotPath =
                getSnapshotPath(pageName);

        Files.createDirectories(
                snapshotPath.getParent()
        );

        Files.writeString(
                snapshotPath,
                html
        );

        System.out.println(
                "DOM snapshot saved:"
        );

        System.out.println(
                snapshotPath.toAbsolutePath()
        );
    }


    public void replaceSnapshot(
            String pageName,
            String html)
            throws IOException {

        Path snapshotPath =
                getSnapshotPath(pageName);

        Files.deleteIfExists(
                snapshotPath
        );

        Files.writeString(
                snapshotPath,
                html
        );

        System.out.println(
                "DOM snapshot replaced:"
        );

        System.out.println(
                snapshotPath.toAbsolutePath()
        );
    }
}
