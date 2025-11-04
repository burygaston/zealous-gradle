package com.taskmanager.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Google Home Page.
 * Demonstrates basic Selenium page object pattern.
 */
public class GoogleHomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Page elements using @FindBy annotations
    @FindBy(name = "q")
    private WebElement searchBox;

    @FindBy(name = "btnK")
    private WebElement searchButton;

    @FindBy(css = "div#search")
    private WebElement searchResults;

    public GoogleHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigate to Google home page
     */
    public void navigateTo() {
        driver.get("https://www.google.com");
    }

    /**
     * Enter search query in search box
     */
    public void enterSearchQuery(String query) {
        wait.until(ExpectedConditions.visibilityOf(searchBox));
        searchBox.clear();
        searchBox.sendKeys(query);
    }

    /**
     * Click the search button
     */
    public void clickSearchButton() {
        // Press Enter instead of clicking button (more reliable)
        searchBox.submit();
    }

    /**
     * Wait for search results to appear
     */
    public void waitForSearchResults() {
        wait.until(ExpectedConditions.visibilityOf(searchResults));
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Check if search results are displayed
     */
    public boolean areSearchResultsDisplayed() {
        try {
            return searchResults.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the number of search results
     */
    public int getSearchResultCount() {
        return driver.findElements(By.cssSelector("div.g")).size();
    }
}
