package Locator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaselineSnapshotManager {

    private final Path baselineDirectory;

    public BaselineSnapshotManager(String baselineDirectory) {
        this.baselineDirectory =
                Paths.get(baselineDirectory);
    }

    /**
     * Returns true if a baseline snapshot exists
     * for the specified page.
     */
    public boolean baselineExists(String pageName) {

        return Files.exists(
                getBaselinePath(pageName)
        );
    }

    /**
     * Returns the baseline snapshot path.
     */
    public Path getBaselinePath(String pageName) {

        return baselineDirectory.resolve(
                pageName + "_baseline.html"
        );
    }

    /**
     * Reads the existing baseline DOM.
     */
    public String loadBaseline(String pageName) {

        Path baselinePath =
                getBaselinePath(pageName);

        if (!Files.exists(baselinePath)) {

            throw new RuntimeException(
                    "Baseline snapshot not found: "
                            + baselinePath
            );
        }

        try {

            return Files.readString(
                    baselinePath
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read baseline snapshot: "
                            + baselinePath,
                    e
            );
        }
    }

    /**
     * Creates or replaces the baseline
     * with the supplied DOM.
     */
    public void saveBaseline(
            String pageName,
            String html) {

        try {

            Files.createDirectories(
                    baselineDirectory
            );

            Path baselinePath =
                    getBaselinePath(pageName);

            Files.writeString(
                    baselinePath,
                    html
            );

            System.out.println(
                    "Baseline snapshot saved:"
            );

            System.out.println(
                    baselinePath.toAbsolutePath()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save baseline snapshot",
                    e
            );
        }
    }
}