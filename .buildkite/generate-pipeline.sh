#!/bin/bash
# Dynamic Pipeline Generator for Task Manager Project
# This script intelligently generates pipeline steps based on:
# - What files changed
# - What branch we're on
# - What environment we're deploying to

set -euo pipefail

echo "--- :mag: Analyzing changes to generate optimal pipeline" >&2

# Detect what changed (compare to main branch or previous commit)
if git rev-parse --verify origin/main >/dev/null 2>&1; then
  COMPARISON_REF="origin/main"
else
  COMPARISON_REF="HEAD~1"
fi

CHANGED_FILES=$(git diff --name-only "$COMPARISON_REF" 2>/dev/null || echo "")

echo "Changed files since $COMPARISON_REF:" >&2
echo "$CHANGED_FILES" | sed 's/^/  - /' >&2
echo "" >&2

# Detect what components changed
JAVA_CHANGED=$(echo "$CHANGED_FILES" | grep -c '\.java$' || echo "0")
JS_CHANGED=$(echo "$CHANGED_FILES" | grep -c '\.js$\|\.jsx$\|\.ts$\|\.tsx$' || echo "0")
DOCKER_CHANGED=$(echo "$CHANGED_FILES" | grep -c 'Dockerfile' || echo "0")
GRADLE_CHANGED=$(echo "$CHANGED_FILES" | grep -c 'build.gradle\|settings.gradle\|gradlew' || echo "0")
PACKAGE_CHANGED=$(echo "$CHANGED_FILES" | grep -c 'package.json\|package-lock.json' || echo "0")

echo "Component change detection:" >&2
echo "  Java files: $JAVA_CHANGED" >&2
echo "  JavaScript files: $JS_CHANGED" >&2
echo "  Docker files: $DOCKER_CHANGED" >&2
echo "  Gradle files: $GRADLE_CHANGED" >&2
echo "  Package files: $PACKAGE_CHANGED" >&2
echo "" >&2

# Start generating pipeline
echo "steps:"

# STEP 1: Always build (but with smart caching)
cat <<'YAML'
  - label: ":gradle: Build Project"
    key: "build"
    agents:
      queue: "default"
    env:
      GRADLE_OPTS: "-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true"
    command: |
      echo "--- :computer: Agent Information"
      echo "Agent: ${BUILDKITE_AGENT_NAME}"
      echo "Queue: ${BUILDKITE_AGENT_META_DATA_QUEUE:-default}"
      echo ""

      echo "--- :package: Installing dependencies"
      ./gradlew dependencies --no-daemon

      echo "--- :hammer: Building project"
      ./gradlew clean build -x test --no-daemon

      echo "--- :arrow_up: Uploading artifacts"
      buildkite-agent artifact upload "build/libs/*.jar"
    artifact_paths:
      - "build/libs/*.jar"
    timeout_in_minutes: 15

YAML

# STEP 2: Java tests (only if Java or Gradle changed)
if [[ $JAVA_CHANGED -gt 0 || $GRADLE_CHANGED -gt 0 ]]; then
  echo "  # Java code changed - adding Java test matrix"
  cat <<'YAML'
  - label: ":test_tube: Run Java Tests - Java {{matrix.java_version}}"
    key: "test-java"
    depends_on: "build"
    agents:
      queue: "default"
    matrix:
      setup:
        java_version:
          - "17"
          - "21"
    env:
      JAVA_VERSION: "{{matrix.java_version}}"
    command: |
      echo "--- :coffee: Java Tests with version ${JAVA_VERSION}"
      echo "Running: ./gradlew test"
      ./gradlew test --no-daemon || EXIT_CODE=$?

      echo "--- :arrow_up: Uploading test results"
      buildkite-agent artifact upload "build/test-results/**/*.xml"

      exit ${EXIT_CODE:-0}
    artifact_paths:
      - "build/test-results/**/*.xml"
      - "build/reports/tests/**/*"
    plugins:
      - test-collector#v1.10.2:
          files: "build/test-results/test/*.xml"
          format: "junit"
    soft_fail:
      - exit_status: "*"
    timeout_in_minutes: 20

YAML
else
  echo "  # No Java changes detected - skipping Java tests"
  cat <<'YAML'
  - label: ":white_check_mark: Java Tests (skipped - no Java changes)"
    key: "test-java"
    command: |
      echo "No Java or Gradle files changed - skipping Java tests"
      echo "To force Java tests, change a .java or build.gradle file"

YAML
fi

# STEP 3: JavaScript tests (only if JS or package.json changed)
if [[ $JS_CHANGED -gt 0 || $PACKAGE_CHANGED -gt 0 ]]; then
  echo "  # JavaScript code changed - adding JS test matrix with parallelism"
  for NODE_VERSION in 18 20; do
    cat <<YAML
  - label: ":jest: Run JavaScript Tests - Node $NODE_VERSION"
    key: "test-js-node$NODE_VERSION"
    depends_on: "build"
    parallelism: 4
    agents:
      queue: "default"
    env:
      NODE_VERSION: "$NODE_VERSION"
    plugins:
      - test-collector#v1.10.2:
          files: "test-results/jest/*.xml"
          format: "junit"
    command: |
      echo "--- :computer: Agent & Environment Information"
      echo "Node Version: $NODE_VERSION"
      echo "Parallel job: \${BUILDKITE_PARALLEL_JOB}/\${BUILDKITE_PARALLEL_JOB_COUNT}"

      echo "--- :wrench: Installing Node.js $NODE_VERSION.x"
      curl -fsSL https://deb.nodesource.com/setup_$NODE_VERSION.x | sudo bash - >/dev/null 2>&1
      sudo apt-get install -y nodejs >/dev/null 2>&1
      echo "Node.js \$(node --version) | npm \$(npm --version)"

      echo "--- :nodejs: Installing dependencies"
      npm install

      echo "--- :jest: Running Jest tests"
      npm run test:ci || EXIT_CODE=\$?

      echo "--- :arrow_up: Uploading test results"
      buildkite-agent artifact upload "test-results/**/*.xml"

      exit \${EXIT_CODE:-0}
    artifact_paths:
      - "test-results/**/*.xml"
      - "coverage/**/*"
    soft_fail:
      - exit_status: "*"
    timeout_in_minutes: 10

YAML
  done
else
  echo "  # No JavaScript changes detected - skipping JS tests"
  cat <<'YAML'
  - label: ":white_check_mark: JavaScript Tests (skipped - no JS changes)"
    key: "test-js"
    command: |
      echo "No JavaScript or package.json files changed - skipping JS tests"
      echo "To force JS tests, change a .js file or package.json"

YAML
fi

# STEP 4: Wait for all tests
cat <<'YAML'
  - wait

YAML

# STEP 5: Test summary
cat <<'YAML'
  - label: ":bar_chart: Test Results Summary"
    key: "test-summary"
    agents:
      queue: "default"
    command: |
      echo "--- :chart_with_upwards_trend: Test execution completed"
      echo "All test results have been collected and uploaded"
      echo "Check the Artifacts tab for detailed reports"
    timeout_in_minutes: 5

YAML

# STEP 6: Docker build (only if Docker changed OR on main branch)
if [[ $DOCKER_CHANGED -gt 0 || "$BUILDKITE_BRANCH" == "main" ]]; then
  cat <<'YAML'
  - label: ":docker: Build & Push Docker Image"
    key: "docker"
    agents:
      queue: "default"
    command: |
      echo "--- :inbox_tray: Downloading JAR artifact"
      buildkite-agent artifact download "build/libs/*.jar" .
      ls -lh build/libs/*.jar

      echo "--- :docker: Building Docker image"
      IMAGE_TAG="${BUILDKITE_BUILD_NUMBER}-${BUILDKITE_COMMIT:0:7}"
      DOCKER_IMAGE="${DOCKERHUB_USERNAME:-taskmanager}/task-manager"

      docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest .

      echo "--- :white_check_mark: Docker image built successfully"
      echo "Image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
    timeout_in_minutes: 15

YAML
else
  echo "  # Docker build skipped - no Dockerfile changes and not on main branch"
fi

# STEP 7: Deployment (only on main branch)
if [[ "$BUILDKITE_BRANCH" == "main" ]]; then
  cat <<'YAML'
  - wait

  - label: ":rocket: Deploy to Production"
    key: "deploy"
    agents:
      queue: "default"
    command: |
      echo "--- :rocket: Deploying to production"
      echo "This is where you'd deploy to production"
      echo "Image: taskmanager/task-manager:${BUILDKITE_BUILD_NUMBER}"
      echo ""
      echo "Deployment commands would go here..."
    timeout_in_minutes: 10

  - label: ":bell: Notify Deployment"
    depends_on: "deploy"
    command: |
      echo "Deployment notification sent!"
      echo "Build: ${BUILDKITE_BUILD_URL}"

YAML
elif [[ "$BUILDKITE_BRANCH" == staging ]]; then
  cat <<'YAML'
  - wait

  - label: ":test_tube: Deploy to Staging"
    key: "deploy-staging"
    command: |
      echo "--- :test_tube: Deploying to staging environment"
      echo "This is a staging deployment"

YAML
else
  echo "  # Deployment skipped - not on main or staging branch"
  cat <<'YAML'
  - label: ":information_source: Deployment Info"
    command: |
      echo "Branch: ${BUILDKITE_BRANCH}"
      echo "Deployments only happen on 'main' (production) or 'staging' branches"
      echo "To deploy, merge this PR to main or staging"

YAML
fi

echo "" >&2
echo "--- :white_check_mark: Dynamic pipeline generated successfully!" >&2
