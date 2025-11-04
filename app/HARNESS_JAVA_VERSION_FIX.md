# Harness Test Intelligence - Java Version Fix

## Error

```
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring root project 'zealous-gradle'.
> Could not resolve all files for configuration ':classpath'.
   > Could not resolve org.springframework.boot:spring-boot-gradle-plugin:3.2.0.
      > No matching variant... compatible with Java 11
        Incompatible because this component declares... Java 17
```

## Root Cause

**Your project requires Java 17**, but Harness is running with **Java 11**.

Spring Boot 3.2.0 requires minimum Java 17:
- Spring Boot 3.x = Java 17+
- Spring Boot 2.x = Java 11+

## Solution: Configure Java 17 in Harness Pipeline

Update your RunTests step to specify Java 17:

### Option 1: Use Runtime Input (Recommended)

```yaml
- step:
    type: RunTests
    name: Run Tests with Intelligence
    identifier: run_tests
    spec:
      connectorRef: <your_docker_connector>
      image: openjdk:17-jdk-slim  # ← Specify Java 17 image
      shell: Bash
      language: Java
      buildTool: Gradle
      args: test --continue --no-daemon
      packages: com.taskmanager
      runOnlySelectedTests: true
      jdk: JDK_17  # ← Explicitly set JDK version
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
            - "app/build/test-results/cucumber/*.xml"
```

### Option 2: Use Container Step with Java 17

```yaml
- step:
    type: Run
    name: Setup Java 17
    identifier: setup_java
    spec:
      shell: Bash
      command: |
        # Verify Java version
        java -version

- step:
    type: RunTests
    name: Run Tests
    identifier: run_tests
    spec:
      connectorRef: account.harnessImage
      image: gradle:8.5-jdk17  # ← Use Gradle image with JDK 17
      shell: Bash
      language: Java
      buildTool: Gradle
      args: test --continue --no-daemon
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
            - "app/build/test-results/cucumber/*.xml"
```

### Option 3: Install Java 17 in the Pipeline

```yaml
- step:
    type: Run
    name: Install Java 17
    identifier: install_java
    spec:
      shell: Bash
      command: |
        # Install Java 17
        apt-get update
        apt-get install -y openjdk-17-jdk

        # Set Java 17 as default
        update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

        # Verify
        java -version

- step:
    type: RunTests
    name: Run Tests
    identifier: run_tests
    spec:
      language: Java
      buildTool: Gradle
      args: test --continue --no-daemon
      reports:
        type: JUnit
        spec:
          paths:
            - "app/build/test-results/test/*.xml"
            - "app/build/test-results/cucumber/*.xml"
```

## Recommended: Complete Pipeline Configuration

```yaml
pipeline:
  name: Test with Intelligence - Java 17
  identifier: test_intelligence_java17
  projectIdentifier: your_project
  orgIdentifier: your_org
  stages:
    - stage:
        name: Build and Test
        identifier: build_test
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
                    image: gradle:8.5-jdk17  # ← Java 17!
                    shell: Bash
                    language: Java
                    buildTool: Gradle
                    args: test --continue --no-daemon
                    packages: com.taskmanager
                    runOnlySelectedTests: true
                    preCommand: |-
                      # Verify Java version
                      echo "Java version:"
                      java -version

                      # Verify Gradle can resolve dependencies
                      ./gradlew dependencies --configuration runtimeClasspath | head -20
                    postCommand: |-
                      # Debug: Show generated reports
                      echo "=== JUnit Test Results ==="
                      find app/build/test-results/test -name "*.xml" -type f | wc -l

                      echo "=== Cucumber Test Results ==="
                      find app/build/test-results/cucumber -name "*.xml" -type f | wc -l

                      # Archive Cucumber HTML reports
                      mkdir -p /harness/cucumber-reports
                      cp -r app/build/reports/cucumber/* /harness/cucumber-reports/ 2>/dev/null || true
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - "app/build/test-results/test/*.xml"
                          - "app/build/test-results/cucumber/*.xml"
                    enableTestSplitting: false
```

## Path Fix for Multi-Module Project

The error also shows:
```
stat /harness/build/test-results/cucumber: no such file or directory
```

This is because Harness is looking at `/harness/build/...` but your project structure has the module prefix.

**Fix the paths**:
```yaml
paths:
  - "app/build/test-results/test/*.xml"       # ✅ Correct
  - "app/build/test-results/cucumber/*.xml"   # ✅ Correct
```

**NOT**:
```yaml
paths:
  - "build/test-results/test/*.xml"           # ❌ Wrong - missing 'app/'
  - "build/test-results/cucumber/*.xml"       # ❌ Wrong - missing 'app/'
```

## Verification Steps

### 1. Verify Java Version Locally

Your local environment:
```bash
java -version
# Should show: openjdk version "17" or "21"
```

### 2. Verify Build Works Locally

```bash
./gradlew clean test --no-daemon
echo "Exit code: $?"
```

### 3. Verify Report Paths

```bash
find app/build/test-results -name "*.xml" -type f
# Should show files in app/build/test-results/test/ and app/build/test-results/cucumber/
```

## Docker Image Recommendations

Choose one of these Docker images with Java 17:

| Image | Description | Size |
|-------|-------------|------|
| `gradle:8.5-jdk17` | Gradle 8.5 + JDK 17 | Recommended |
| `gradle:8.5-jdk17-alpine` | Alpine Linux (smaller) | Good for CI |
| `openjdk:17-jdk-slim` | Just OpenJDK 17 | If using Gradle wrapper |
| `eclipse-temurin:17-jdk` | Eclipse Adoptium JDK | Alternative |

## Summary

The error `"no tests found in the summary"` is happening because:

1. **Primary Issue**: Java 11 vs Java 17 incompatibility
   - Spring Boot 3.2.0 requires Java 17
   - Harness is using Java 11
   - Build fails before tests run

2. **Secondary Issue**: Path mismatch
   - Harness looking for `/harness/build/...`
   - Actual location: `/harness/app/build/...`

**Fix Both**:
1. Use `image: gradle:8.5-jdk17` in your pipeline
2. Ensure paths include `app/` prefix

Once the build succeeds, the test reports will be generated and Harness TI will be able to read them.

## About BDD Test Names

Regarding your question about BDD-style test names:

**YES, they work with Harness Test Intelligence!**

The Cucumber JUnit XML format is:
```xml
<testcase classname="Common Utility Functions" name="Get application name" .../>
```

Harness TI will show:
- **Test Class**: "Common Utility Functions" (your Feature name)
- **Test Name**: "Get application name" (your Scenario name)

This is **better** than the JUnit wrapper which shows:
- **Test Class**: "com.taskmanager.cucumber.RunCucumberTest"
- **Test Name**: "Get application name"

The BDD names make it easier to understand which business features are being tested!
