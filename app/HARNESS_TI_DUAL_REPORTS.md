# Harness TI with Dual Test Reports (JUnit + Cucumber)

This guide shows how to configure Harness Test Intelligence to read **both** JUnit test results and Cucumber test results as separate report sets.

## Report Structure

After running `./gradlew test`, you'll have test results in **two separate locations**:

### 1. Standard JUnit Test Results
**Location**: `app/build/test-results/test/*.xml`

Contains:
- All JUnit tests (120 tests)
- JUnit Platform wrapper for Cucumber (RunCucumberTest)
- Individual test class results

**Example files**:
```
app/build/test-results/test/
├── TEST-com.taskmanager.FlakyTest.xml
├── TEST-com.taskmanager.cucumber.RunCucumberTest.xml
├── TEST-com.taskmanager.model.LabelTest.xml
├── TEST-com.taskmanager.model.UserTest.xml
├── TEST-com.taskmanager.model.WorkItemTest.xml
├── TEST-com.taskmanager.service.EmailServiceTest.xml
├── TEST-com.taskmanager.service.LabelServiceTest.xml
├── TEST-com.taskmanager.service.ReportServiceTest.xml
├── TEST-com.taskmanager.service.UserServiceTest.xml
└── TEST-com.taskmanager.service.WorkItemServiceTest.xml
```

###  2. Cucumber-Specific JUnit XML
**Location**: `app/build/test-results/cucumber/cucumber.xml`

Contains:
- Cucumber scenarios as JUnit test cases (3 scenarios)
- Feature names as classnames
- Scenario names as test names
- Step-level details in system-out

**Example content**:
```xml
<testsuite name="Cucumber" time="0.073" tests="3" ...>
  <testcase classname="Common Utility Functions" name="Get application name" time="0.01">
    <system-out><![CDATA[
Given the application is running............................................passed
When I request the application name.........................................passed
Then the application name should be "Hello from Task Manager"...............passed
]]></system-out>
  </testcase>
  <!-- More scenarios... -->
</testsuite>
```

## Harness Pipeline Configuration

To have Harness TI read **both** report locations, use multiple path patterns:

### Recommended Configuration

```yaml
- step:
    type: RunTests
    name: Run All Tests with Intelligence
    identifier: run_all_tests
    spec:
      language: Java
      buildTool: Gradle
      args: test --continue --no-daemon
      packages: com.taskmanager
      runOnlySelectedTests: true
      postCommand: |-
        # Verify both report types were generated
        echo "=== JUnit Test Results ==="
        find app/build/test-results/test -name "*.xml" -type f | wc -l

        echo "=== Cucumber Test Results ==="
        find app/build/test-results/cucumber -name "*.xml" -type f | wc -l

        # Archive Cucumber HTML reports for viewing
        mkdir -p /harness/cucumber-reports
        cp -r app/build/reports/cucumber/* /harness/cucumber-reports/ 2>/dev/null || true
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"           # JUnit tests
            - "app/build/test-results/cucumber/*.xml"        # Cucumber tests
      enableTestSplitting: false
```

### Alternative: Wildcard Pattern

If you want a simpler configuration that captures both:

```yaml
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/**/*.xml"  # Captures both directories
```

## What Harness TI Will See

With the dual-path configuration, Harness Test Intelligence will:

1. **Read JUnit tests** from `test/` directory
   - 120 individual JUnit test cases
   - Standard JUnit test execution data

2. **Read Cucumber tests** from `cucumber/` directory
   - 3 Cucumber scenarios
   - Feature-level organization
   - BDD-style test names

3. **Display both** in the Harness TI dashboard
   - Total: 123 tests (120 JUnit + 3 Cucumber)
   - Separate tracking for each test type
   - Unified test analytics

## Benefits of Dual Reports

### 1. Separate Test Tracking
- JUnit tests tracked independently from Cucumber tests
- Different test selection strategies for each type
- Clear separation in analytics

### 2. Better Test Names
Cucumber XML has feature-based names:
- Classname: `"Common Utility Functions"` (Feature name)
- Testname: `"Get application name"` (Scenario name)

vs JUnit wrapper:
- Classname: `"com.taskmanager.cucumber.RunCucumberTest"`
- Testname: `"Get application name"`

### 3. Complete Coverage
- All test types captured in Harness TI
- No duplicate counting (Cucumber tests appear once)
- Full test history for both JUnit and BDD tests

## Verification

### Local Verification

After running tests locally:

```bash
# Run all tests
./gradlew clean test --continue

# Check JUnit results
ls -la app/build/test-results/test/*.xml

# Check Cucumber results
ls -la app/build/test-results/cucumber/*.xml

# View Cucumber XML content
cat app/build/test-results/cucumber/cucumber.xml
```

Expected output:
- 10 XML files in `test/` directory
- 1 XML file in `cucumber/` directory
- Cucumber XML shows feature and scenario names

### In Harness

After pipeline execution, verify:

1. **Test Execution Tab**: Should show 123 total tests
2. **Test Reports**: Should list both JUnit and Cucumber tests
3. **Test Trends**: Both test types tracked over time

## Running Tests Separately

### Only JUnit Tests

```yaml
args: test --tests '!*RunCucumberTest*' --continue --no-daemon
reports:
  spec:
    paths:
      - "app/build/test-results/test/*.xml"
```

### Only Cucumber Tests

```yaml
args: test --tests '*RunCucumberTest*' --continue --no-daemon
reports:
  spec:
    paths:
      - "app/build/test-results/cucumber/*.xml"
```

## Troubleshooting

### Issue: Only seeing JUnit tests, not Cucumber

**Solution**: Ensure both paths are specified:
```yaml
paths:
  - "app/build/test-results/test/*.xml"
  - "app/build/test-results/cucumber/*.xml"
```

### Issue: Seeing duplicate Cucumber tests

This happens if both reports are read. To avoid:
- Use separate paths (recommended)
- Or exclude RunCucumberTest from JUnit results

### Issue: Cucumber XML not generated

**Solution**: Verify RunCucumberTest.java has correct plugin configuration:
```java
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "..., junit:build/test-results/cucumber/cucumber.xml"
)
```

## Summary

✅ **Dual report configuration allows**:
- **JUnit tests**: Read from `test/` directory (120 tests)
- **Cucumber tests**: Read from `cucumber/` directory (3 scenarios)
- **Harness TI**: Sees all 123 tests with proper categorization
- **No duplicates**: Each test counted once
- **Better organization**: Feature-based names for Cucumber tests

This setup gives you the best of both worlds: traditional JUnit test reporting AND BDD-style Cucumber test reporting, all tracked by Harness Test Intelligence.
