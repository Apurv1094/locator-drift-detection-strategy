package Locator;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

public class HistoricalSnapshotResolver {

    private final Path snapshotDirectory;

    public HistoricalSnapshotResolver(String snapshotDirectory) {
        this.snapshotDirectory = Paths.get(snapshotDirectory);
    }

    public Path getLatestSnapshot(String pageName) {

        if (!Files.exists(snapshotDirectory)) {
            throw new RuntimeException(
                    "Snapshot directory does not exist: "
                            + snapshotDirectory.toAbsolutePath()
            );
        }

        try (Stream<Path> files = Files.list(snapshotDirectory)) {

            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName()
                            .toString()
                            .startsWith(pageName + "_"))
                    .filter(path -> path.getFileName()
                            .toString()
                            .endsWith(".html"))
                    .max(Comparator.comparing(Path::getFileName))
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No snapshot found for page: " + pageName
                            )
                    );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read snapshot directory", e
            );
        }
    }

    public String readSnapshot(Path snapshotPath) {

        try {
            return Files.readString(snapshotPath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read snapshot: " + snapshotPath, e
            );
        }
    }
}