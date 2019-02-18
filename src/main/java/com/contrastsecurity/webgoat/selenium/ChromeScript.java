package com.contrastsecurity.webgoat.selenium;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeScript {
    public static void run(String un, String pw, String url, boolean headless, String driverPath, String browserBin) {
        ChromeOptions chromeOptions = new ChromeOptions();
        if (!browserBin.equals("null")) {
            chromeOptions.addExtensions(new File(browserBin));
        }

        chromeOptions.addArguments("--verbose");

        if (headless) {
            chromeOptions.addArguments("--headless");
        }
        if (System.getProperty("os.name").startsWith("Windows")) {
            chromeOptions.addArguments("--disable-gpu");
        }
        System.setProperty("webdriver.chrome.driver", driverPath);
        WebDriver driver = new ChromeDriver(chromeOptions);

        try {
            driver.get(url + "/login");
            driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);

            // Login
            driver.findElement(By.name("username")).sendKeys(un);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.className("btn")).click();

            // Check if user exists.  If not, create user.
            if (driver.getCurrentUrl().equals(url + "/login?error")) {
                driver.get(url + "/registration");
                driver.findElement(By.id("username")).sendKeys(un);
                driver.findElement(By.id("password")).sendKeys(pw);
                driver.findElement(By.id("matchingPassword")).sendKeys(pw);
                driver.findElement(By.name("agree")).click();
                driver.findElement(By.className("btn-primary")).click();
            }

            // Navigate to String SQL Injection section
            driver.get(url + "/start.mvc#lesson/SqlInjection.lesson/6");
            delay(1000);
            //retryingFindSendKeys(driver, By.xpath("//*[@id=\"lesson-content-wrapper\"]/div[6]/div[9]/div[2]/form/table/tbody/tr/td[2]/input"), "' OR '1'='1");
            driver.findElement(By.name("account")).sendKeys("anyAccount6");
            driver.findElement(By.name("Get Account Info")).click();

            // Navigate to Numeric SQL Injection section
            driver.get(url + "/start.mvc#lesson/SqlInjection.lesson/7");
            driver.findElement(By.name("userid")).sendKeys("anyAccount7");
            driver.findElement(By.xpath("/html/body/section/section/section/div[1]/div[1]/div/div/div/div[6]/div[10]/div[2]/form/table/tbody/tr/td[3]/input")).click();
        
            System.out.println("Successfully finished Chrome script!");
        } finally {
            driver.quit();
        }
    }

    private static void retryingFindSendKeys(WebDriver driver, By by, String text) {
        //boolean result = false;
        int attempts = 0;
        while(attempts < 100) {
            try {
                driver.findElement(by).sendKeys(text);
                //result = true;
                break;
            } catch(StaleElementReferenceException e) {}
            attempts++;
        }
    }

    private static void delay (long time) {
        try {
            Thread.sleep(time);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
