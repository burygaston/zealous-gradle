package com.taskmanager.selenium.tests;

import com.taskmanager.selenium.base.BaseSeleniumTest;
import com.taskmanager.selenium.pages.GoogleHomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Selenium tests using TestNG for Google Search functionality.
 * Demonstrates how Selenium + TestNG tests can be converted to JUnit XML format
 * for Harness Test Intelligence.
 */
public class GoogleSearchSeleniumTest extends BaseSeleniumTest {

    @Test(description = "Verify Google home page loads successfully")
    public void testGoogleHomePageLoads() {
        GoogleHomePage homePage = new GoogleHomePage(driver);
        homePage.navigateTo();

        String pageTitle = homePage.getPageTitle();
        Assert.assertTrue(pageTitle.contains("Google"),
                "Page title should contain 'Google' but was: " + pageTitle);
    }

    @Test(description = "Verify search functionality works")
    public void testSearchFunctionality() {
        GoogleHomePage homePage = new GoogleHomePage(driver);
        homePage.navigateTo();

        String searchQuery = "Harness Test Intelligence";
        homePage.enterSearchQuery(searchQuery);
        homePage.clickSearchButton();
        homePage.waitForSearchResults();

        Assert.assertTrue(homePage.areSearchResultsDisplayed(),
                "Search results should be displayed after searching");
        Assert.assertTrue(homePage.getSearchResultCount() > 0,
                "Search should return at least one result");
    }

    @Test(description = "Verify search query in Selenium", priority = 1)
    public void testSearchQuerySelenium() {
        GoogleHomePage homePage = new GoogleHomePage(driver);
        homePage.navigateTo();

        homePage.enterSearchQuery("Selenium WebDriver");
        homePage.clickSearchButton();
        homePage.waitForSearchResults();

        Assert.assertTrue(homePage.areSearchResultsDisplayed(),
                "Search results should be displayed for Selenium query");
    }

    @Test(description = "Verify search query in TestNG", priority = 2)
    public void testSearchQueryTestNG() {
        GoogleHomePage homePage = new GoogleHomePage(driver);
        homePage.navigateTo();

        homePage.enterSearchQuery("TestNG Framework");
        homePage.clickSearchButton();
        homePage.waitForSearchResults();

        Assert.assertTrue(homePage.areSearchResultsDisplayed(),
                "Search results should be displayed for TestNG query");
    }

    @Test(description = "Verify page title changes after search", priority = 3)
    public void testPageTitleAfterSearch() {
        GoogleHomePage homePage = new GoogleHomePage(driver);
        homePage.navigateTo();

        String originalTitle = homePage.getPageTitle();

        homePage.enterSearchQuery("Java Gradle");
        homePage.clickSearchButton();
        homePage.waitForSearchResults();

        String newTitle = homePage.getPageTitle();
        Assert.assertNotEquals(newTitle, originalTitle,
                "Page title should change after performing a search");
        Assert.assertTrue(newTitle.contains("Java Gradle"),
                "Page title should contain the search query");
    }
}
