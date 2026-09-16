package Locator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DomSnapshotManager {

    private static final String SNAPSHOT_DIRECTORY =
            "snapshots";

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Saves the supplied page source as an HTML snapshot.
     *
     * @param pageName  logical page name, for example "login"
     * @param pageSource current Selenium page source
     * @return path of the saved snapshot
     */
    public Path saveSnapshot(
            String pageName,
            String pageSource) throws IOException {

        if (pageSource == null || pageSource.isBlank()) {
            throw new IllegalArgumentException(
                    "Page source cannot be empty."
            );
        }

        String timestamp =
                LocalDateTime.now()
                        .format(TIMESTAMP_FORMAT);

        Path snapshotDirectory =
                Paths.get(SNAPSHOT_DIRECTORY);

        Files.createDirectories(snapshotDirectory);

        String fileName =
                pageName + "_" + timestamp + ".html";

        Path snapshotPath =
                snapshotDirectory.resolve(fileName);

        Files.writeString(
                snapshotPath,
                pageSource,
                StandardCharsets.UTF_8
        );

        System.out.println(
                "\nDOM snapshot saved:"
        );

        System.out.println(
                snapshotPath.toAbsolutePath()
        );

        return snapshotPath;
    }
}
