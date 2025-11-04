# Harness Test Intelligence - Troubleshooting Guide

## Error: "no tests found in the summary"

This error occurs when Harness TI cannot find or parse test results. Here are the solutions:

### Solution 1: Continue on Test Failures

The most common cause is test failures causing Gradle to exit with a non-zero code, preventing Harness from reading the results.

**Fix**: Add `continueOnError: true` or use `|| true` to ensure reports are generated even if tests fail:

```yaml
- step:
    type: RunTests
    name: Run Tests with Intelligence
    identifier: run_tests
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

**Or use Run step with explicit handling**:

```yaml
- step:
    type: Run
    name: Run Tests
    identifier: run_tests
    spec:
      shell: Bash
      command: |
        #!/bin/bash
        set +e  # Don't exit on error
        ./gradlew clean test --no-daemon
        TEST_EXIT_CODE=$?

        echo "Test execution completed with exit code: $TEST_EXIT_CODE"

        # List generated reports for verification
        echo "Generated test reports:"
        find app/build/test-results -name "*.xml" -type f

        exit $TEST_EXIT_CODE
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
```

### Solution 2: Verify Path Pattern

Ensure the path pattern matches your actual test results location.

**Check locally**:
```bash
./gradlew test
find app/build/test-results -name "*.xml" -type f
```

**Common patterns**:
- Single module: `build/test-results/test/*.xml`
- Multi-module (our case): `app/build/test-results/test/*.xml`
- All modules: `*/build/test-results/test/*.xml`
- Recursive: `**/build/test-results/test/*.xml`

### Solution 3: Use Absolute Paths

Sometimes relative paths don't work correctly. Try absolute paths:

```yaml
reports:
  type: JUnit
  spec:
    paths:
      - "/harness/app/build/test-results/test/*.xml"
```

### Solution 4: Gradle Test Task Configuration

Ensure Gradle doesn't skip test result generation on failure:

```gradle
// In build.gradle
tasks.named('test') {
    useJUnitPlatform()

    // Always generate reports even on failure
    ignoreFailures = false

    reports {
        junitXml.required = true
        html.required = true
    }

    // Ensure results are always written
    finalizedBy 'jacocoTestReport'  // Optional
}
```

### Solution 5: Debug Report Generation

Add debugging to see what files are being generated:

```yaml
- step:
    type: Run
    name: Run Tests
    identifier: run_tests
    spec:
      shell: Bash
      command: |
        # Run tests
        ./gradlew clean test --no-daemon || true

        # Debug: List all XML files
        echo "=== Test Result Files ==="
        find . -name "*.xml" -path "*/test-results/*" -type f

        # Debug: Show file count
        echo "=== File Count ==="
        find app/build/test-results/test -name "*.xml" -type f | wc -l

        # Debug: Show sample content
        echo "=== Sample XML Content ==="
        find app/build/test-results/test -name "*.xml" -type f | head -1 | xargs cat | head -20
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
```

### Solution 6: Handle Flaky Tests

Your repository has intentional flaky tests. Either:

**A. Exclude them temporarily**:
```bash
./gradlew test --tests '!*FlakyTest*'
```

**B. Or handle failures gracefully**:
```yaml
- step:
    type: RunTests
    name: Run Tests
    spec:
      args: test --continue  # Continue even if some tests fail
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
```

## Recommended Harness Pipeline Configuration

### Option 1: RunTests Step (Recommended)

```yaml
stages:
  - stage:
      name: Test
      identifier: test
      type: CI
      spec:
        cloneCodebase: true
        execution:
          steps:
            - step:
                type: RunTests
                name: Run All Tests
                identifier: run_all_tests
                spec:
                  language: Java
                  buildTool: Gradle
                  args: test --continue --no-daemon
                  packages: com.taskmanager
                  runOnlySelectedTests: true
                  postCommand: |-
                    # Debug: Verify reports were generated
                    echo "=== Generated Test Reports ==="
                    find app/build/test-results/test -name "*.xml" -type f -ls

                    # Archive Cucumber HTML reports
                    mkdir -p /harness/cucumber-reports
                    cp -r app/build/reports/cucumber/* /harness/cucumber-reports/ 2>/dev/null || true
                  reports:
                    type: JUnit
                    spec:
                      paths:
                        - "app/build/test-results/test/*.xml"
                  enableTestSplitting: false
```

### Option 2: Run Step with Explicit Control

```yaml
- step:
    type: Run
    name: Execute Tests
    identifier: execute_tests
    spec:
      shell: Bash
      command: |
        #!/bin/bash
        set -x  # Debug mode

        # Run tests and capture exit code
        ./gradlew clean test --no-daemon --continue
        GRADLE_EXIT=$?

        # Always list generated reports
        echo "===  Test Results Generated ==="
        find app/build/test-results -type f -name "*.xml" -exec echo {} \;

        # Count test files
        TEST_COUNT=$(find app/build/test-results/test -name "*.xml" -type f | wc -l)
        echo "Found $TEST_COUNT test result files"

        if [ "$TEST_COUNT" -eq 0 ]; then
          echo "ERROR: No test results found!"
          exit 1
        fi

        # Exit with original Gradle exit code
        exit $GRADLE_EXIT
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
```

## Verification Steps

1. **Run locally**:
   ```bash
   ./gradlew clean test
   ls -la app/build/test-results/test/*.xml
   ```

2. **Check XML format**:
   ```bash
   cat app/build/test-results/test/TEST-*.xml | head -20
   ```

3. **Verify test count**:
   ```bash
   grep -h "tests=" app/build/test-results/test/*.xml | head -5
   ```

## Common Issues

### Issue: "Could not find or load main class"
**Solution**: Ensure Java is available in the Harness environment.

### Issue: Reports not found after successful test run
**Solution**: Double-check the path pattern matches your project structure.

### Issue: Some tests not appearing
**Solution**: Ensure all test classes are being picked up by JUnit Platform.

### Issue: Cucumber tests not showing
**Solution**: Verify `RunCucumberTest.java` is in the correct package and has `@Suite` annotation.

## Success Indicators

When working correctly, you should see:
- ✅ Test execution completes (even if some tests fail)
- ✅ XML files are created in `app/build/test-results/test/`
- ✅ Harness TI dashboard shows test results
- ✅ Test analytics appear in Harness UI

## Getting Help

If issues persist:
1. Check Harness logs for the exact error message
2. Verify XML files exist: `ls -la app/build/test-results/test/*.xml`
3. Validate XML format: `xmllint app/build/test-results/test/*.xml`
4. Contact Harness support with build logs
