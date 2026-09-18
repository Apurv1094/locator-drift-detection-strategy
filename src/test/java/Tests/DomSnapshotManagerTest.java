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

            // =========================================================
            // 1. Page configuration
            // =========================================================

            String pageName = "login-page";

            String url =
                    "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";


            // =========================================================
            // 2. Open application
            // =========================================================

            System.out.println(
                    "Opening application..."
            );

            driver.get(url);


            // =========================================================
            // 3. Get current DOM
            // =========================================================

            String pageSource =
                    driver.getPageSource();

            System.out.println(
                    "DOM fetched successfully."
            );


            // =========================================================
            // 4. Create snapshot manager
            // =========================================================

            DomSnapshotManager snapshotManager =
                    new DomSnapshotManager();


            // =========================================================
            // 5. Get snapshot path
            // =========================================================

            Path snapshotPath =
                    snapshotManager.getSnapshotPath(
                            pageName
                    );


            // =========================================================
            // 6. Save / replace snapshot
            // =========================================================
            //
            // replaceSnapshot() is used because the POC maintains
            // only ONE snapshot for each page.
            //
            // Example:
            //
            // snapshots/
            //     login-page.html
            //
            // =========================================================

            snapshotManager.replaceSnapshot(
                    pageName,
                    pageSource
            );


            // =========================================================
            // 7. Verify snapshot
            // =========================================================

            System.out.println(
                    "\nSnapshot created successfully."
            );

            System.out.println(
                    "Snapshot path:"
            );

            System.out.println(
                    snapshotPath.toAbsolutePath()
            );


            // =========================================================
            // 8. Verify that file exists
            // =========================================================

            System.out.println(
                    "\nSnapshot exists: "
                            + snapshotManager.snapshotExists(
                            pageName
                    )
            );


        } finally {

            driver.quit();
        }
    }
}
