package Tests;

import Locator.*;
import Pages.LoginPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class LocatorHealthTest {

    public static void main(String[] args) {

        WebDriver driver =
                new ChromeDriver();

        try {

            // =========================================================
            // CONFIGURATION
            // =========================================================

            String pageName = "login";

            String url =
                    "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

            DomSnapshotManager snapshotManager =
                    new DomSnapshotManager();

            Path snapshotPath =
                    snapshotManager.getSnapshotPath(
                            pageName
                    );

            boolean oldSnapshotExists =
                    snapshotManager.snapshotExists(
                            pageName
                    );

            System.out.println(
                    "\n=============================================="
            );

            System.out.println(
                    "       LOCATOR DRIFT DETECTION POC"
            );

            System.out.println(
                    "=============================================="
            );

            System.out.println(
                    "Page          : "
                            + pageName
            );

            System.out.println(
                    "Snapshot Path : "
                            + snapshotPath.toAbsolutePath()
            );

            System.out.println(
                    "Previous DOM  : "
                            + (
                            oldSnapshotExists
                                    ? "FOUND"
                                    : "NOT FOUND"
                    )
            );

            // =========================================================
            // OPEN APPLICATION
            // =========================================================

            System.out.println(
                    "\n========== OPEN APPLICATION =========="
            );

            System.out.println(
                    "URL: "
                            + url
            );

            driver.get(url);

            WebDriverWait wait =
                    new WebDriverWait(
                            driver,
                            Duration.ofSeconds(15)
                    );

            wait.until(
                    webDriver ->
                            webDriver.getPageSource()
                                    .contains("Username")
            );

            // =========================================================
            // CURRENT DOM
            // =========================================================

            String currentHtml =
                    driver.getPageSource();

            System.out.println(
                    "\n========== CURRENT DOM =========="
            );

            System.out.println(
                    "Page source fetched successfully."
            );

            // =========================================================
            // CURRENT DOM INVENTORY
            // =========================================================

            DomAnalyzer domAnalyzer =
                    new DomAnalyzer(
                            currentHtml
                    );

            System.out.println(
                    "\n========== CURRENT DOM INVENTORY =========="
            );

            domAnalyzer.printElementInventory();

            // =========================================================
            // EXTRACT PAGE OBJECT LOCATORS
            // =========================================================

            PageObjectLocatorExtractor extractor =
                    new PageObjectLocatorExtractor();

            List<LocatorDefinition> locators =
                    extractor.extract(
                            LoginPage.class
                    );

            System.out.println(
                    "\n========== PAGE OBJECT LOCATORS =========="
            );

            System.out.println(
                    "Total locators found: "
                            + locators.size()
            );

            // =========================================================
            // LOCATOR HEALTH
            // =========================================================

            LocatorValidator validator =
                    new LocatorValidator(
                            driver
                    );

            System.out.println(
                    "\n========== LOCATOR HEALTH =========="
            );

            for (LocatorDefinition locator :
                    locators) {

                boolean valid = false;

                String type =
                        locator.getLocatorType();

                String value =
                        locator.getLocatorValue();

                try {

                    if (type.equalsIgnoreCase(
                            "XPATH")) {

                        valid =
                                validator.isValid(
                                        By.xpath(value)
                                );

                    } else if (
                            type.equalsIgnoreCase(
                                    "CSS")) {

                        valid =
                                validator.isValid(
                                        By.cssSelector(value)
                                );
                    }

                } catch (Exception ignored) {
                    valid = false;
                }

                System.out.println(
                        "\nPage Object  : "
                                + locator.getPageObjectFile()
                );

                System.out.println(
                        "Variable     : "
                                + locator.getVariableName()
                );

                System.out.println(
                        "Locator Type : "
                                + type
                );

                System.out.println(
                        "Locator Value: "
                                + value
                );

                System.out.println(
                        "Status       : "
                                + (
                                valid
                                        ? "VALID"
                                        : "BROKEN"
                        )
                );

                System.out.println(
                        "-------------------------------------------"
                );
            }

            // =========================================================
            // OLD DOM vs CURRENT DOM
            // =========================================================

            if (oldSnapshotExists) {

                System.out.println(
                        "\n========== OLD DOM vs CURRENT DOM =========="
                );

                String oldHtml =
                        Files.readString(
                                snapshotPath
                        );

                DomComparator comparator =
                        new DomComparator();

                DomComparisonResult comparison =
                        comparator.compare(
                                oldHtml,
                                currentHtml
                        );

                System.out.println(
                        "Old meaningful elements     : "
                                + comparison.getOldElementCount()
                );

                System.out.println(
                        "Current meaningful elements : "
                                + comparison.getCurrentElementCount()
                );

                System.out.println(
                        "Unchanged elements          : "
                                + comparison.getUnchangedCount()
                );

                System.out.println(
                        "Changed elements            : "
                                + comparison.getChangedCount()
                );

                System.out.println(
                        "Added elements              : "
                                + comparison.getAddedCount()
                );

                System.out.println(
                        "Removed elements            : "
                                + comparison.getRemovedCount()
                );

                if (!comparison.getChanges()
                        .isEmpty()) {

                    System.out.println(
                            "\nDOM CHANGES:"
                    );

                    for (String change :
                            comparison.getChanges()) {

                        System.out.println(
                                "- " + change
                        );
                    }
                } else {

                    System.out.println(
                            "\nNo meaningful DOM changes detected."
                    );
                }

                // =====================================================
                // LOCATOR RECOVERY
                // =====================================================

                LocatorRecoveryEngine recoveryEngine =
                        new LocatorRecoveryEngine(
                                driver
                        );

                for (LocatorDefinition locator :
                        locators) {

                    recoveryEngine.processLocator(
                            locator,
                            snapshotPath,
                            currentHtml
                    );
                }

            } else {

                System.out.println(
                        "\n========== HISTORICAL SNAPSHOT =========="
                );

                System.out.println(
                        "No previous snapshot found."
                );

                System.out.println(
                        "Current DOM will become "
                                + "the initial baseline."
                );
            }

            // =========================================================
            // UPDATE BASELINE
            // =========================================================

            System.out.println(
                    "\n========== SNAPSHOT UPDATE =========="
            );

            snapshotManager.replaceSnapshot(
                    pageName,
                    currentHtml
            );

            System.out.println(
                    "DOM snapshot replaced:"
            );

            System.out.println(
                    snapshotPath.toAbsolutePath()
            );

            System.out.println(
                    "Snapshot updated successfully."
            );

            System.out.println(
                    "New baseline:"
            );

            System.out.println(
                    snapshotPath.toAbsolutePath()
            );

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            driver.quit();

        }

        System.out.println(
                "\n=============================================="
        );

        System.out.println(
                "       LOCATOR HEALTH CHECK COMPLETE"
        );

        System.out.println(
                "=============================================="
        );
    }
}