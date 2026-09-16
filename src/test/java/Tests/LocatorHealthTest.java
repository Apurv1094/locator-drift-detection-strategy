package Tests;

import Locator.*;
import Pages.LoginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class LocatorHealthTest {

    public static void main(String[] args) {

        WebDriver driver = new ChromeDriver();

        try {

            // 1. Open application
            String url =
                    "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

            System.out.println("Opening URL:");
            System.out.println(url);

            driver.get(url);

            // 2. Wait for page to load
            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(10));

            wait.until(
                    webDriver ->
                            webDriver.getPageSource().contains("Username")
            );

            /// 3. Fetch current page source
            String pageSource =
                    driver.getPageSource();

            System.out.println(
                    "\nPage source fetched successfully."
            );

// 3A. Save DOM snapshot
            DomSnapshotManager snapshotManager =
                    new DomSnapshotManager();

            snapshotManager.saveSnapshot(
                    "login",
                    pageSource
            );

            // 4. Analyze current DOM
            DomAnalyzer domAnalyzer =
                    new DomAnalyzer(pageSource);

            domAnalyzer.printElementInventory();

            // 5. Extract locators from Page Object
            PageObjectLocatorExtractor extractor =
                    new PageObjectLocatorExtractor();

            List<LocatorDefinition> locators =
                    extractor.extract(LoginPage.class);

            // 6. Validate Page Object locators
            LocatorValidator validator =
                    new LocatorValidator(driver);

            System.out.println(
                    "\n========== LOCATOR HEALTH =========="
            );

            for (LocatorDefinition locator : locators) {

                boolean valid = false;

                if (locator.getLocatorType().equals("XPATH")) {

                    By seleniumLocator =
                            By.xpath(locator.getLocatorValue());

                    valid =
                            validator.isValid(seleniumLocator);
                }

                System.out.println(
                        "Page Object  : "
                                + locator.getPageObjectFile()
                );

                System.out.println(
                        "Variable     : "
                                + locator.getVariableName()
                );

                System.out.println(
                        "Locator Type : "
                                + locator.getLocatorType()
                );

                System.out.println(
                        "Locator Value: "
                                + locator.getLocatorValue()
                );

                System.out.println(
                        "Status       : "
                                + (valid ? "VALID" : "BROKEN")
                );

                System.out.println(
                        "-------------------------------------------"
                );
            }

            // 7. Recover broken locators
            LocatorRecoveryEngine recoveryEngine =
                    new LocatorRecoveryEngine(driver);

            System.out.println(
                    "\n========== LOCATOR RECOVERY =========="
            );

            for (LocatorDefinition locator : locators) {

                recoveryEngine.processLocator(locator);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            driver.quit();
        }
    }
}