# Harness Test Intelligence with Selenium + TestNG

This guide demonstrates how to integrate **Selenium WebDriver** tests with **TestNG** into Harness Test Intelligence by converting TestNG reports to JUnit XML format.

## Overview

This repository now supports **four testing frameworks**, all integrated with Harness TI:

1. **JUnit 5** - Standard unit tests (120 tests)
2. **Cucumber** - BDD tests with Gherkin syntax (3 scenarios)
3. **Selenium + TestNG** - UI automation tests (9 tests)

All three generate **JUnit-compatible XML reports** that Harness Test Intelligence can read.

## How It Works

### TestNG Native JUnit XML Support

TestNG has **built-in support** for generating JUnit XML reports:

```xml
<!-- testng.xml -->
<suite name="Test Suite">
    <listeners>
        <listener class-name="org.testng.reporters.JUnitXMLReporter"/>
    </listeners>
    <!-- test definitions -->
</suite>
```

When you run TestNG tests via Gradle, the reports are automatically generated in JUnit XML format.

## Report Locations

After running tests, you'll have reports in **three separate locations**:

### 1. JUnit Test Results
**Location**: `app/build/test-results/test/*.xml`

Contains:
- All JUnit 5 tests (120 tests)
- Standard unit tests for services, models, etc.

### 2. Cucumber Test Results
**Location**: `app/build/test-results/cucumber/cucumber.xml`

Contains:
- Cucumber BDD scenarios (3 scenarios)
- Feature-based test organization

### 3. TestNG/Selenium Test Results
**Location**: `app/build/test-results/testng/*.xml`

Contains:
- Selenium UI tests with TestNG (9 tests)
- TestNG-generated JUnit XML format

**Example TestNG JUnit XML**:
```xml
<testsuite name="Google Search Tests" tests="5" failures="0" ...>
  <testcase name="testGoogleHomePageLoads"
            classname="com.taskmanager.selenium.tests.GoogleSearchSeleniumTest"
            time="2.345"/>
  <testcase name="testSearchFunctionality"
            classname="com.taskmanager.selenium.tests.GoogleSearchSeleniumTest"
            time="3.127"/>
</testsuite>
```

## Project Structure

```
app/src/test/java/com/taskmanager/selenium/
├── base/
│   └── BaseSeleniumTest.java         # Base class with WebDriver setup/teardown
├── pages/
│   └── GoogleHomePage.java           # Page Object Model for Google
└── tests/
    ├── GoogleSearchSeleniumTest.java # Google search tests (5 tests)
    └── BasicUISeleniumTest.java      # Basic UI tests (4 tests)

app/src/test/resources/
└── testng.xml                        # TestNG suite configuration
```

## Running Tests

### Run Only Selenium/TestNG Tests

```bash
./gradlew testng
```

This will:
- Run all `*SeleniumTest` and `*UITest` classes
- Generate JUnit XML in `app/build/test-results/testng/*.xml`
- Generate HTML report in `app/build/reports/testng/`

### Run All Tests (JUnit + Cucumber + TestNG)

```bash
./gradlew testAll
```

This runs all three test suites in parallel.

### Run Standard Tests Only (Excludes TestNG)

```bash
./gradlew test
```

This runs only JUnit and Cucumber tests, excluding Selenium/TestNG tests.

## Gradle Configuration

### Dependencies

```gradle
// Selenium WebDriver
testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'

// TestNG
testImplementation 'org.testng:testng:7.8.0'
```

### TestNG Task

```gradle
task testng(type: Test) {
    description = 'Run TestNG tests (Selenium UI tests)'

    useTestNG() {
        suites file('src/test/resources/testng.xml')
        useDefaultListeners = true
        outputDirectory = file("$buildDir/test-results/testng")
    }

    reports {
        junitXml.required = true
        junitXml.outputLocation = file("$buildDir/test-results/testng")
    }
}
```

## Harness Pipeline Configuration

### Option 1: Run All Tests Together

```yaml
- step:
    type: RunTests
    name: Run All Tests (JUnit + Cucumber + TestNG)
    identifier: run_all_tests
    spec:
      connectorRef: account.harnessImage
      image: gradle:8.5-jdk17
      shell: Bash
      language: Java
      buildTool: Gradle
      args: testAll --continue --no-daemon
      packages: com.taskmanager
      runOnlySelectedTests: true
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"        # JUnit tests
            - "app/build/test-results/cucumber/*.xml"    # Cucumber tests
            - "app/build/test-results/testng/*.xml"      # TestNG/Selenium tests
```

### Option 2: Run Tests in Separate Steps

```yaml
steps:
  # Step 1: JUnit + Cucumber
  - step:
      type: RunTests
      name: Run Unit and BDD Tests
      identifier: run_unit_bdd_tests
      spec:
        connectorRef: account.harnessImage
        image: gradle:8.5-jdk17
        args: test --continue --no-daemon
        reports:
          type: JUnit
          spec:
            paths:
              - "app/build/test-results/test/*.xml"
              - "app/build/test-results/cucumber/*.xml"

  # Step 2: Selenium + TestNG
  - step:
      type: RunTests
      name: Run Selenium UI Tests
      identifier: run_selenium_tests
      spec:
        connectorRef: account.harnessImage
        image: gradle:8.5-jdk17
        args: testng --continue --no-daemon
        reports:
          type: JUnit
          spec:
            paths:
              - "app/build/test-results/testng/*.xml"
```

## Complete Pipeline Example

```yaml
pipeline:
  name: Multi-Framework Test Intelligence
  identifier: multi_framework_ti
  projectIdentifier: your_project
  orgIdentifier: your_org
  stages:
    - stage:
        name: Test All Frameworks
        identifier: test_all_frameworks
        type: CI
        spec:
          cloneCodebase: true
          platform:
            os: Linux
            arch: Amd64
          runtime:
            type: Cloud
            spec: {}
          execution:
            steps:
              - step:
                  type: RunTests
                  name: Run All Tests
                  identifier: run_all_tests
                  spec:
                    connectorRef: account.harnessImage
                    image: gradle:8.5-jdk17
                    shell: Bash
                    language: Java
                    buildTool: Gradle
                    args: testAll --continue --no-daemon
                    packages: com.taskmanager
                    runOnlySelectedTests: true
                    preCommand: |-
                      # Verify Java version
                      echo "=== Java Version ==="
                      java -version

                      # Install Chrome for Selenium tests
                      echo "=== Installing Chrome ==="
                      apt-get update
                      apt-get install -y wget gnupg
                      wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
                      echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
                      apt-get update
                      apt-get install -y google-chrome-stable

                      # Verify Chrome installation
                      google-chrome --version
                    postCommand: |-
                      # Debug: Show generated reports
                      echo "=== JUnit Test Results ==="
                      find app/build/test-results/test -name "*.xml" -type f | wc -l

                      echo "=== Cucumber Test Results ==="
                      find app/build/test-results/cucumber -name "*.xml" -type f | wc -l

                      echo "=== TestNG/Selenium Test Results ==="
                      find app/build/test-results/testng -name "*.xml" -type f | wc -l

                      # Archive HTML reports
                      mkdir -p /harness/test-reports
                      cp -r app/build/reports/* /harness/test-reports/ 2>/dev/null || true
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - "app/build/test-results/test/*.xml"
                          - "app/build/test-results/cucumber/*.xml"
                          - "app/build/test-results/testng/*.xml"
                    enableTestSplitting: false
```

## TestNG Features Demonstrated

### 1. Page Object Model (POM)

```java
public class GoogleHomePage {
    private final WebDriver driver;

    @FindBy(name = "q")
    private WebElement searchBox;

    public GoogleHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterSearchQuery(String query) {
        searchBox.sendKeys(query);
    }
}
```

### 2. Test Groups

```java
@Test(groups = {"smoke"})
public void testExampleDotComLoads() {
    // Test code
}

@Test(groups = {"regression"})
public void testExampleDotComParagraph() {
    // Test code
}
```

### 3. Test Priorities

```java
@Test(priority = 1)
public void testSearchQuerySelenium() {
    // Runs first
}

@Test(priority = 2)
public void testSearchQueryTestNG() {
    // Runs second
}
```

### 4. Test Descriptions

```java
@Test(description = "Verify Google home page loads successfully")
public void testGoogleHomePageLoads() {
    // Description appears in reports
}
```

## What Harness TI Will See

With the triple-path configuration, Harness Test Intelligence will display:

1. **JUnit Tests**: 120 tests from `test/` directory
   - `com.taskmanager.service.UserServiceTest`
   - `com.taskmanager.model.WorkItemTest`
   - etc.

2. **Cucumber Tests**: 3 scenarios from `cucumber/` directory
   - Feature: "Common Utility Functions"
   - Scenarios: "Get application name", "Call new method V3", etc.

3. **TestNG/Selenium Tests**: 9 tests from `testng/` directory
   - `com.taskmanager.selenium.tests.GoogleSearchSeleniumTest`
   - `com.taskmanager.selenium.tests.BasicUISeleniumTest`

**Total**: 132 tests tracked by Harness TI

## Benefits

### 1. Multi-Framework Support
- Unit tests (JUnit)
- BDD tests (Cucumber)
- UI tests (Selenium + TestNG)
- All in one Harness TI dashboard

### 2. Intelligent Test Selection
Harness TI can:
- Run only UI tests affected by frontend changes
- Skip UI tests when only backend changes
- Track test performance across frameworks

### 3. Unified Reporting
- Single source of truth for all test types
- Consistent analytics across frameworks
- Cross-framework test trends

## Selenium Best Practices

### 1. Headless Mode for CI/CD

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
```

### 2. WebDriverManager

Uses `WebDriverManager` to automatically download and setup ChromeDriver:

```java
WebDriverManager.chromedriver().setup();
```

No need to manually download or specify driver paths!

### 3. Explicit Waits

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOf(element));
```

## Troubleshooting

### Issue: Chrome not found in CI

**Solution**: Install Chrome in preCommand:

```yaml
preCommand: |-
  apt-get update
  apt-get install -y wget gnupg
  wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
  echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
  apt-get update
  apt-get install -y google-chrome-stable
```

### Issue: TestNG tests not found

**Solution**: Verify testng.xml path in build.gradle:

```gradle
useTestNG() {
    suites file('src/test/resources/testng.xml')
}
```

### Issue: Selenium tests timeout

**Solution**: Increase timeout or use faster assertions:

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
```

### Issue: Tests run in JUnit task instead of TestNG

**Solution**: Exclude Selenium tests from JUnit:

```gradle
test {
    exclude '**/*SeleniumTest*'
    exclude '**/*UITest*'
}
```

## Local Verification

### 1. Run TestNG tests locally

```bash
./gradlew testng --info
```

### 2. Verify JUnit XML generation

```bash
ls -la app/build/test-results/testng/*.xml
```

Expected: Multiple XML files, one per test suite.

### 3. View TestNG HTML report

```bash
open app/build/reports/testng/index.html
```

### 4. Check XML format

```bash
cat app/build/test-results/testng/TEST-Google_Search_Tests.xml
```

Should show JUnit-compatible XML with `<testsuite>` and `<testcase>` elements.

## Summary

✅ **Selenium + TestNG integration complete!**

This repository now demonstrates a **multi-framework testing POV** with:

- **JUnit 5**: Fast unit tests
- **Cucumber**: BDD with Gherkin
- **Selenium + TestNG**: UI automation

All generating **JUnit XML reports** that Harness Test Intelligence can:
- Parse and analyze
- Enable intelligent test selection
- Provide unified test analytics
- Track trends across all frameworks

This proves that Harness TI can handle **any testing framework** as long as it generates JUnit-compatible XML reports!
