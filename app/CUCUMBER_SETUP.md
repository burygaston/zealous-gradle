# Cucumber BDD Testing Setup

This project now includes Cucumber BDD (Behavior-Driven Development) testing alongside existing JUnit tests.

## What's Added

### Dependencies
- `io.cucumber:cucumber-java` - Core Cucumber Java implementation
- `io.cucumber:cucumber-junit-platform-engine` - JUnit Platform integration
- `org.junit.platform:junit-platform-suite` - JUnit Platform Suite API

### Test Structure

```
app/src/test/
├── java/com/taskmanager/cucumber/
│   ├── RunCucumberTest.java        # JUnit Platform test runner
│   ├── CommonUtilSteps.java        # Step definitions
└── resources/features/
    └── common-util.feature          # Feature files (Gherkin syntax)
```

## Reports Generated

When you run `./gradlew test`, Cucumber generates the following reports in addition to JUnit reports:

### JUnit Reports (existing)
- **HTML**: `app/build/reports/tests/test/index.html`
- **XML**: `app/build/test-results/test/*.xml`

### Cucumber Reports (new)
- **HTML**: `app/build/reports/cucumber/cucumber-report.html`
- **JSON**: `app/build/reports/cucumber/cucumber.json`
- **XML**: `app/build/reports/cucumber/cucumber.xml`

## Running Cucumber Tests

```bash
# Run all tests (includes Cucumber tests)
./gradlew test

# Run only Cucumber tests
./gradlew test --tests "*RunCucumberTest"

# Run specific feature
./gradlew test --tests "*RunCucumberTest" -Dcucumber.filter.tags="@yourTag"
```

## Writing New Cucumber Tests

### 1. Create a Feature File
Create `.feature` files in `app/src/test/resources/features/`:

```gherkin
Feature: Your Feature Name
  Description of the feature

  Scenario: Your scenario description
    Given some precondition
    When some action is performed
    Then some outcome is expected
```

### 2. Implement Step Definitions
Create step definition classes in `app/src/test/java/com/taskmanager/cucumber/`:

```java
@Given("some precondition")
public void somePrecondition() {
    // Implementation
}

@When("some action is performed")
public void someActionIsPerformed() {
    // Implementation
}

@Then("some outcome is expected")
public void someOutcomeIsExpected() {
    // Assertions
}
```

## Example Test

The project includes a sample test for `CommonUtil`:
- **Feature**: `app/src/test/resources/features/common-util.feature`
- **Steps**: `app/src/test/java/com/taskmanager/cucumber/CommonUtilSteps.java`

## Viewing Reports

After running tests:
1. **Open JUnit HTML Report**: `open app/build/reports/tests/test/index.html`
2. **Open Cucumber HTML Report**: `open app/build/reports/cucumber/cucumber-report.html`

The Cucumber HTML report provides a beautiful, feature-oriented view of your test results.

## CI/CD Integration

Both JUnit and Cucumber XML reports can be consumed by CI/CD tools like Jenkins, GitLab CI, GitHub Actions, etc.

### Harness Test Intelligence

Cucumber tests are **fully compatible with Harness Test Intelligence**! The JUnit Platform generates JUnit-compatible XML reports that Harness TI can read.

See [HARNESS_TI_CUCUMBER.md](./HARNESS_TI_CUCUMBER.md) for detailed setup instructions and pipeline configuration.
