package Locator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LocatorValidator {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LocatorValidator(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }

    public boolean isValid(By locator) {

        try {

            wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );

            List<WebElement> elements =
                    driver.findElements(locator);

            return !elements.isEmpty();

        } catch (Exception e) {

            return false;
        }
    }
}