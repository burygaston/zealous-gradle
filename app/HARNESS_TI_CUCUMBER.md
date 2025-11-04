# Harness Test Intelligence with Cucumber

This repository is configured to work with Harness Test Intelligence (TI), including both JUnit and Cucumber tests.

## How It Works

Cucumber tests generate **JUnit-compatible XML reports** that Harness Test Intelligence can read and analyze.

### Report Locations

When you run `./gradlew test`, test results are generated in **two separate locations**:

#### 1. JUnit XML Reports (Standard Tests)
Located in `app/build/test-results/test/`:
- `TEST-*.xml` - All JUnit test results (120 tests)
- `TEST-com.taskmanager.cucumber.RunCucumberTest.xml` - JUnit Platform wrapper

#### 2. Cucumber JUnit XML (Separate Report)
Located in `app/build/test-results/cucumber/`:
- `cucumber.xml` - Cucumber-generated JUnit XML with feature/scenario names (3 scenarios)

**Harness TI can read both** to:
- Track test execution results from JUnit AND Cucumber
- Identify which tests need to run based on code changes
- Generate unified test reports and analytics
- See feature-based names for Cucumber tests

#### Additional Reports (for human viewing)
Located in `app/build/reports/cucumber/`:
- `cucumber-report.html` - Beautiful HTML report
- `cucumber.json` - JSON format for CI/CD integrations

**Want to configure both report paths?** See [HARNESS_TI_DUAL_REPORTS.md](./HARNESS_TI_DUAL_REPORTS.md) for the complete dual-report configuration guide.

## Harness Pipeline Configuration

### Step 1: Run Tests with Harness TI

**Important**: Use `--continue` flag to ensure all tests run and reports are generated even if some tests fail.

```yaml
- step:
    type: RunTests
    name: Run Tests with Intelligence
    identifier: run_tests
    spec:
      language: Java
      buildTool: Gradle
      args: test --continue --no-daemon
      packages: com.taskmanager
      runOnlySelectedTests: true
      postCommand: |-
        # Debug: Verify reports were generated
        echo "=== Generated Test Reports ==="
        find app/build/test-results/test -name "*.xml" -type f -ls || echo "No XML files found"

        # Optional: Archive Cucumber HTML reports
        mkdir -p /harness/cucumber-reports
        cp -r app/build/reports/cucumber/* /harness/cucumber-reports/ 2>/dev/null || true
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"      # JUnit tests
            - "app/build/test-results/cucumber/*.xml"   # Cucumber tests (separate)
      enableTestSplitting: false
```

**Notes**:
- The `--continue` flag ensures Gradle generates XML reports even when tests fail, preventing the "no tests found" error.
- Using **two path patterns** allows Harness to read both JUnit and Cucumber test results separately
- See [HARNESS_TI_DUAL_REPORTS.md](./HARNESS_TI_DUAL_REPORTS.md) for detailed explanation

### Step 2: View Results

Harness Test Intelligence will automatically:
1. Parse all JUnit XML files (including Cucumber tests)
2. Display test results in the Harness UI
3. Track test trends over time
4. Show which tests failed and why
5. Enable intelligent test selection on future runs

## Test Report Structure

The JUnit XML report for Cucumber tests contains:

```xml
<testsuite name="com.taskmanager.cucumber.RunCucumberTest" tests="3" ...>
  <testcase name="Get application name" classname="..." time="0.037"/>
  <testcase name="Call new method V3" classname="..." time="0.004"/>
  <testcase name="Call new method V5" classname="..." time="0.004"/>
  <system-out>
    <!-- Cucumber scenario details with Given/When/Then steps -->
  </system-out>
</testsuite>
```

Each Cucumber **Scenario** becomes a JUnit **testcase**, making them trackable by Harness TI.

## Benefits

### 1. Test Intelligence
- **Smart Test Selection**: Run only tests affected by code changes
- **Test Analytics**: Track test performance and flakiness
- **Faster Feedback**: Skip unaffected tests to reduce build time

### 2. Unified Reporting
- Both JUnit and Cucumber tests appear in the same Harness TI dashboard
- Single source of truth for all test results
- Consistent test analytics across test types

### 3. BDD Benefits with TI
- Feature-level test tracking (each scenario is a test case)
- Business-readable test names in Harness UI
- Link features to code changes

## Example: Harness Pipeline YAML

```yaml
pipeline:
  name: Build and Test with TI
  identifier: build_test_ti
  stages:
    - stage:
        name: Test
        identifier: test
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
                    language: Java
                    buildTool: Gradle
                    args: clean test
                    runOnlySelectedTests: true
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - "app/build/test-results/test/*.xml"
```

## Viewing Cucumber-Specific Reports

While Harness TI uses the JUnit XML, you can still access beautiful Cucumber HTML reports:

### In CI/CD
Add this to your pipeline to archive Cucumber HTML reports:

```yaml
postCommand: |-
  mkdir -p /harness/artifacts/cucumber
  cp -r app/build/reports/cucumber/*.html /harness/artifacts/cucumber/
```

### Locally
```bash
./gradlew test
open app/build/reports/cucumber/cucumber-report.html
```

## Best Practices

1. **Run all tests in one command**: `./gradlew test`
   - This ensures both JUnit and Cucumber tests run together
   - Harness TI can intelligently select from both test types

2. **Use meaningful scenario names**:
   - Good: `Scenario: User can create a new task`
   - Bad: `Scenario: Test 1`
   - Scenario names appear in Harness TI reports

3. **Tag your features**:
   ```gherkin
   @critical @user-management
   Feature: User Authentication
   ```
   - Use tags for selective test execution
   - Filter tests in Harness TI based on tags

## Troubleshooting

### Issue: "no tests found in the summary"

This is the most common error. **Solution**: Add `--continue` flag to your Gradle args:

```yaml
args: test --continue --no-daemon
```

This ensures reports are generated even if tests fail. See [HARNESS_TI_TROUBLESHOOTING.md](./HARNESS_TI_TROUBLESHOOTING.md) for detailed solutions.

### Issue: Cucumber tests not appearing in Harness TI

**Solution**: Verify the JUnit XML is being generated:
```bash
./gradlew clean test
ls -la app/build/test-results/test/TEST-*.xml
```

### Issue: Test selection not working

**Solution**: Ensure `runOnlySelectedTests: true` is set in your Harness pipeline step.

### Issue: Want to run only Cucumber tests

**Solution**:
```bash
./gradlew test --tests "*RunCucumberTest"
```

Or in Harness:
```yaml
args: test --tests "*RunCucumberTest"
```

### Need More Help?

See the comprehensive [HARNESS_TI_TROUBLESHOOTING.md](./HARNESS_TI_TROUBLESHOOTING.md) guide for:
- Detailed error solutions
- Alternative pipeline configurations
- Debug commands
- Path pattern examples

## Summary

✅ **Yes, Cucumber tests work with Harness Test Intelligence!**

The setup uses the standard JUnit Platform integration, which generates JUnit-compatible XML reports that Harness TI can parse. You get the best of both worlds:
- BDD-style testing with Cucumber
- Intelligent test selection and analytics from Harness TI
- Beautiful Cucumber HTML reports for detailed analysis
