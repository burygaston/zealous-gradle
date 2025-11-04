# Testing Frameworks Overview - Harness TI POV

This repository demonstrates how **Harness Test Intelligence** can work with **multiple testing frameworks** by converting their native report formats to JUnit XML.

## Supported Frameworks

| Framework | Test Type | Test Count | Report Location | Status |
|-----------|-----------|------------|-----------------|--------|
| **JUnit 5** | Unit Tests | 120 tests | `app/build/test-results/test/*.xml` | ✅ Working |
| **Cucumber** | BDD Tests | 3 scenarios | `app/build/test-results/cucumber/cucumber.xml` | ✅ Working |
| **Selenium + TestNG** | UI Tests | 9 tests | `app/build/test-results/testng/*.xml` | ✅ Working |

**Total Tests**: 132 tests across 3 frameworks

## Quick Start

### Run All Tests

```bash
./gradlew testAll --continue
```

This runs:
- JUnit 5 unit tests
- Cucumber BDD tests
- Selenium + TestNG UI tests

### Run Tests by Framework

```bash
# JUnit + Cucumber only
./gradlew test --continue

# Selenium + TestNG only
./gradlew testng --continue
```

### View Test Reports Locally

```bash
# JUnit/Cucumber HTML report
open app/build/reports/tests/test/index.html

# Cucumber HTML report
open app/build/reports/cucumber/cucumber-report.html

# TestNG HTML report
open app/build/reports/testng/index.html
```

## Report Conversion to JUnit XML

All three frameworks generate **JUnit-compatible XML** that Harness TI can parse:

### 1. JUnit 5 (Native)
- **Native Format**: JUnit XML
- **Conversion**: None needed
- **Reports**: `TEST-*.xml` files

### 2. Cucumber (Plugin)
- **Native Format**: Cucumber JSON
- **Conversion**: `junit:` plugin
- **Configuration**:
  ```java
  @ConfigurationParameter(
      key = PLUGIN_PROPERTY_NAME,
      value = "junit:build/test-results/cucumber/cucumber.xml"
  )
  ```

### 3. TestNG (Built-in Reporter)
- **Native Format**: TestNG XML
- **Conversion**: `JUnitXMLReporter` listener
- **Configuration**:
  ```xml
  <suite name="Test Suite">
      <listeners>
          <listener class-name="org.testng.reporters.JUnitXMLReporter"/>
      </listeners>
  </suite>
  ```

## Harness Pipeline Configuration

### Single Step - All Tests

```yaml
- step:
    type: RunTests
    name: Run All Tests
    spec:
      image: gradle:8.5-jdk17
      args: testAll --continue --no-daemon
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"        # JUnit
            - "app/build/test-results/cucumber/*.xml"    # Cucumber
            - "app/build/test-results/testng/*.xml"      # TestNG
```

### Multi-Step - Separate Framework Runs

```yaml
steps:
  # Unit + BDD Tests
  - step:
      type: RunTests
      name: Unit and BDD Tests
      spec:
        args: test --continue --no-daemon
        reports:
          spec:
            paths:
              - "app/build/test-results/test/*.xml"
              - "app/build/test-results/cucumber/*.xml"

  # UI Tests
  - step:
      type: RunTests
      name: Selenium UI Tests
      spec:
        args: testng --continue --no-daemon
        preCommand: |
          # Install Chrome for Selenium
          apt-get update && apt-get install -y google-chrome-stable
        reports:
          spec:
            paths:
              - "app/build/test-results/testng/*.xml"
```

## Documentation

Detailed guides for each framework:

- **[HARNESS_TI_CUCUMBER.md](./HARNESS_TI_CUCUMBER.md)** - Cucumber + Harness TI integration
- **[HARNESS_TI_SELENIUM_TESTNG.md](./HARNESS_TI_SELENIUM_TESTNG.md)** - Selenium + TestNG integration
- **[HARNESS_TI_DUAL_REPORTS.md](./HARNESS_TI_DUAL_REPORTS.md)** - Multi-framework report configuration
- **[HARNESS_JAVA_VERSION_FIX.md](./HARNESS_JAVA_VERSION_FIX.md)** - Java 17 configuration for Harness
- **[HARNESS_TI_TROUBLESHOOTING.md](./HARNESS_TI_TROUBLESHOOTING.md)** - Common errors and solutions
- **[HARNESS_PIPELINE_EXAMPLE.yaml](./HARNESS_PIPELINE_EXAMPLE.yaml)** - Complete working pipeline

## Test Examples

### JUnit 5 Example

```java
@Test
void shouldCreateNewUser() {
    User user = new User("john.doe@example.com", "password123");
    assertNotNull(user);
}
```

**Report Output**:
```xml
<testcase name="shouldCreateNewUser"
          classname="com.taskmanager.model.UserTest"
          time="0.012"/>
```

### Cucumber Example

```gherkin
Scenario: Get application name
  Given the application is running
  When I request the application name
  Then the application name should be "Hello from Task Manager"
```

**Report Output**:
```xml
<testcase name="Get application name"
          classname="Common Utility Functions"
          time="0.037"/>
```

### TestNG Example

```java
@Test(description = "Verify Google home page loads successfully")
public void testGoogleHomePageLoads() {
    GoogleHomePage homePage = new GoogleHomePage(driver);
    homePage.navigateTo();
    Assert.assertTrue(homePage.getPageTitle().contains("Google"));
}
```

**Report Output**:
```xml
<testcase name="testGoogleHomePageLoads"
          classname="com.taskmanager.selenium.tests.GoogleSearchSeleniumTest"
          time="2.345"/>
```

## Key Learnings - POV Summary

### 1. Framework Agnostic
✅ Harness TI works with **any testing framework** that can generate JUnit XML

### 2. Native vs Converted Reports
- **JUnit**: Native JUnit XML format
- **Cucumber**: Uses `junit:` plugin to convert
- **TestNG**: Uses `JUnitXMLReporter` listener to convert

### 3. Multi-Framework Dashboard
All 132 tests appear in a **single unified Harness TI dashboard** with:
- Test execution results
- Test analytics and trends
- Intelligent test selection
- Flakiness detection

### 4. Separate vs Combined Execution
You can:
- Run all frameworks together (`testAll`)
- Run frameworks separately (`test`, `testng`)
- Configure different report paths for each

### 5. Framework-Specific Features Preserved
- **Cucumber**: Feature and Scenario names in reports
- **TestNG**: Test groups, priorities, and descriptions
- **JUnit**: Standard unit test structure

## Benefits for POV

This multi-framework setup demonstrates:

1. **Flexibility**: Harness TI adapts to your existing test structure
2. **No Vendor Lock-in**: Use your preferred testing frameworks
3. **Unified Analytics**: Single dashboard for all test types
4. **Intelligent Selection**: TI works across all frameworks
5. **Easy Migration**: Convert reports using built-in tools/plugins

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Gradle Build                              │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ JUnit Tests  │  │Cucumber Tests│  │TestNG Tests  │      │
│  │              │  │              │  │              │      │
│  │ 120 tests    │  │ 3 scenarios  │  │ 9 tests      │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │              │
│         ▼                 ▼                  ▼              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Native XML  │  │junit: plugin │  │JUnitXMLReport│      │
│  │              │  │              │  │   listener   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │              │
│         └─────────────────┴──────────────────┘              │
│                           │                                  │
│                           ▼                                  │
│              ┌─────────────────────────┐                    │
│              │   JUnit XML Reports     │                    │
│              │   (3 separate paths)    │                    │
│              └────────────┬────────────┘                    │
└───────────────────────────┼─────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  Harness Test           │
              │  Intelligence           │
              │                         │
              │  • Parse all reports    │
              │  • Unified dashboard    │
              │  • Intelligent selection│
              │  • Analytics & trends   │
              └─────────────────────────┘
```

## Next Steps

1. **Update Harness Pipeline**: Use the configuration from `HARNESS_PIPELINE_EXAMPLE.yaml`
2. **Install Chrome**: Add Chrome installation to `preCommand` for Selenium tests
3. **Configure Java 17**: Use `gradle:8.5-jdk17` Docker image
4. **Run Tests**: Execute pipeline and view results in Harness TI dashboard

## Verification Checklist

- [x] All dependencies added to build.gradle
- [x] JUnit XML reports generated for all frameworks
- [x] Gradle tasks configured (test, testng, testAll)
- [x] Test exclusions configured (TestNG tests excluded from JUnit task)
- [x] Documentation created for each framework
- [x] Example tests created for all three frameworks
- [x] Page Object Model demonstrated (Selenium)
- [x] TestNG features demonstrated (groups, priorities, descriptions)
- [x] Cucumber BDD scenarios created
- [ ] Tests verified in Harness TI dashboard (pending Java 17 pipeline update)

## Support

For issues or questions:
- Check troubleshooting guides in documentation files
- Review Harness TI documentation
- Contact Harness support for pipeline-specific issues
