package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class LoginPage {

    private static String uname = "//*[@name='user_name']";
    private static String pwd = "input[name='paword']";
    private static String login = "//button[normalize-space()='Login']";

//    static WebDriver driver = new ChromeDriver();
//
//    public static void login(String username, String password) {
//
//        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        WebElement usernameField = driver.findElement(By.xpath(uname));
//        usernameField.sendKeys("Admin");
//        WebElement passwordField = driver.findElement(By.xpath(pwd));
//        passwordField.sendKeys("admin123");
//        WebElement loginButton = driver.findElement(By.xpath(login));
//        loginButton.click();
//    }

}


