package com.taskmanager.selenium.tests;

import com.taskmanager.selenium.base.BaseSeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Basic UI tests using Selenium + TestNG.
 * Tests example.com to demonstrate simple UI testing.
 */
public class BasicUISeleniumTest extends BaseSeleniumTest {

    @Test(description = "Verify example.com loads successfully", groups = {"smoke"})
    public void testExampleDotComLoads() {
        driver.get("https://example.com");

        String pageTitle = driver.getTitle();
        Assert.assertEquals(pageTitle, "Example Domain",
                "Page title should be 'Example Domain'");
    }

    @Test(description = "Verify example.com heading text", groups = {"smoke"})
    public void testExampleDotComHeading() {
        driver.get("https://example.com");

        WebElement heading = driver.findElement(By.tagName("h1"));
        String headingText = heading.getText();

        Assert.assertEquals(headingText, "Example Domain",
                "Heading should be 'Example Domain'");
    }

    @Test(description = "Verify example.com paragraph exists", groups = {"regression"})
    public void testExampleDotComParagraph() {
        driver.get("https://example.com");

        WebElement paragraph = driver.findElement(By.tagName("p"));
        String paragraphText = paragraph.getText();

        Assert.assertTrue(paragraphText.contains("illustrative examples"),
                "Paragraph should contain 'illustrative examples'");
    }

    @Test(description = "Verify More information link exists", groups = {"regression"})
    public void testMoreInformationLinkExists() {
        driver.get("https://example.com");

        WebElement link = driver.findElement(By.linkText("More information..."));
        Assert.assertTrue(link.isDisplayed(),
                "More information link should be displayed");
        Assert.assertTrue(link.getAttribute("href").contains("iana.org"),
                "Link should point to iana.org");
    }
}
