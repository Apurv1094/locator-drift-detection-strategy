package Tests;

import Locator.DomSnapshotManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.nio.file.Path;

public class DomSnapshotManagerTest {

    public static void main(String[] args) throws IOException {

        WebDriver driver = new ChromeDriver();

        try {

            String url =
                    "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

            // 1. Open application
            driver.get(url);

            // 2. Get current DOM
            String pageSource =
                    driver.getPageSource();

            System.out.println(
                    "DOM fetched successfully."
            );

            // 3. Create snapshot manager
            DomSnapshotManager snapshotManager =
                    new DomSnapshotManager();

            // 4. Save DOM snapshot
            Path snapshotPath =
                    snapshotManager.saveSnapshot(
                            "login-page",
                            pageSource
                    );

            // 5. Verify snapshot path
            System.out.println(
                    "\nSnapshot created successfully."
            );

            System.out.println(
                    "Snapshot path:"
            );

            System.out.println(
                    snapshotPath.toAbsolutePath()
            );

        } finally {

            driver.quit();
        }
    }
}