# Dynamic Pipeline for Task Manager

This directory contains a **dynamic pipeline** that intelligently generates build steps based on what code has changed.

## Overview

Instead of running all tests for every commit, the dynamic pipeline:
- ✅ Analyzes what files changed
- ✅ Only runs relevant tests
- ✅ Skips unnecessary steps
- ✅ Optimizes build time
- ✅ Provides clear feedback about what's running and why

## Files

### `pipeline-dynamic.yml`
The entry point that triggers the pipeline generator.

### `generate-pipeline.sh`
The intelligent script that:
1. Detects what files changed (Java, JavaScript, Docker, etc.)
2. Generates appropriate test steps
3. Decides whether to build Docker images
4. Adds deployment steps for main/staging branches

## How It Works

### Step 1: Detection
The script compares your branch to `origin/main` and identifies:
- **Java changes** (*.java files)
- **JavaScript changes** (*.js, *.jsx, *.ts, *.tsx files)
- **Docker changes** (Dockerfile)
- **Gradle changes** (build.gradle, settings.gradle)
- **Package changes** (package.json, package-lock.json)

### Step 2: Generation
Based on what changed:

| What Changed | What Runs |
|-------------|-----------|
| Java files  | ✅ Java tests (matrix: Java 17 & 21) |
| JS files    | ✅ JavaScript tests (matrix: Node 18 & 20, parallelism: 4) |
| Nothing | ⏭️ Tests skipped (build only) |
| Dockerfile | ✅ Docker build & push |
| Main branch | ✅ Production deployment |

### Step 3: Execution
The generated pipeline runs with optimal resource usage.

## Example Scenarios

### Scenario 1: Only Java Changed
```bash
# You changed: WorkItemController.java

Pipeline generates:
1. Build (always)
2. Java Tests - Java 17 ✅
3. Java Tests - Java 21 ✅
4. JS Tests - SKIPPED ⏭️
5. Summary
6. Docker - SKIPPED (not main branch)
```

### Scenario 2: Only JavaScript Changed
```bash
# You changed: app.test.js

Pipeline generates:
1. Build (always)
2. Java Tests - SKIPPED ⏭️
3. JS Tests - Node 18 (parallelism: 4) ✅
4. JS Tests - Node 20 (parallelism: 4) ✅
5. Summary
```

### Scenario 3: Main Branch (Deploy)
```bash
# Branch: main
# Changed: WorkItemController.java

Pipeline generates:
1. Build (always)
2. Java Tests - Java 17 & 21 ✅
3. Summary
4. Docker Build & Push ✅
5. Deploy to Production ✅
6. Notify Deployment ✅
```

### Scenario 4: Only Documentation Changed
```bash
# You changed: README.md

Pipeline generates:
1. Build (always)
2. Java Tests - SKIPPED ⏭️
3. JS Tests - SKIPPED ⏭️
4. Summary
5. "No code changes detected" message
```

## Using the Dynamic Pipeline

### Option 1: In Buildkite UI
1. Go to Pipeline Settings
2. Change pipeline file path to: `.buildkite/pipeline-dynamic.yml`

### Option 2: Test Locally
Run the generator script manually to see what it would generate:

```bash
# Dry run - see what would be generated
./.buildkite/generate-pipeline.sh
```

### Option 3: Force Full Pipeline
To override the smart detection and run everything:

```bash
# Commit a change to build.gradle to force all tests
git commit --allow-empty -m "Force full pipeline run"
```

## Benefits

### Time Savings
- **Before**: Every commit runs ~15 minutes of tests
- **After**: Only relevant tests run
  - Java-only change: ~8 minutes
  - JS-only change: ~5 minutes
  - Docs-only change: ~2 minutes (build only)

### Resource Efficiency
- Don't spin up 8 JavaScript test agents when only Java changed
- Don't run Java tests when only fixing typos
- Don't build Docker images for every branch

### Developer Experience
- Faster feedback on PRs
- Clear indication of what's running and why
- Helpful messages when steps are skipped

## Customization

### Add New Detection Rules
Edit `generate-pipeline.sh` and add detection logic:

```bash
# Example: Detect CSS changes
CSS_CHANGED=$(echo "$CHANGED_FILES" | grep -c '\.css$' || echo "0")

if [[ $CSS_CHANGED -gt 0 ]]; then
  # Add CSS linting step
fi
```

### Modify Branch Logic
Change deployment conditions:

```bash
# Deploy to staging on develop branch
if [[ "$BUILDKITE_BRANCH" == "develop" ]]; then
  # Add staging deployment
fi
```

### Adjust Parallelism
Modify the parallelism settings in the script:

```bash
parallelism: 4  # Change to 2, 8, etc.
```

## Debugging

### See What Was Detected
The pipeline shows detection results in the first step:

```
Changed files since origin/main:
  - src/main/java/com/taskmanager/controller/WorkItemController.java

Component change detection:
  Java files: 1
  JavaScript files: 0
  Docker files: 0
```

### Force Specific Tests
Create temporary files to trigger tests:

```bash
# Force Java tests
touch src/main/java/Dummy.java
git add . && git commit -m "Trigger Java tests"

# Force JS tests
touch src/test/javascript/dummy.test.js
git add . && git commit -m "Trigger JS tests"
```

## Comparison: Static vs Dynamic

### Static Pipeline (Original)
```yaml
# Always runs all steps
- Build
- Java Tests (Java 17)
- Java Tests (Java 21)
- JS Tests (Node 18, parallelism: 4)
- JS Tests (Node 20, parallelism: 4)
- Docker Build
```
**Total:** Always uses 10 agents (2 + 8), ~15 minutes

### Dynamic Pipeline (This)
```yaml
# Only runs what's needed
- Build
- [Conditional] Java Tests if Java changed
- [Conditional] JS Tests if JS changed
- [Conditional] Docker if Dockerfile changed or main branch
```
**Total:** 2-10 agents depending on changes, 2-15 minutes

## Advanced: Multi-Stage Dynamic

You can even generate pipelines in stages:

```yaml
# Stage 1: Run tests
# Stage 2: If tests pass, generate deployment pipeline
# Stage 3: Deploy
```

See the [Buildkite Dynamic Pipelines docs](https://buildkite.com/docs/pipelines/defining-steps#dynamic-pipelines) for more.

## Troubleshooting

### "No files changed" but you know something changed
The script compares to `origin/main`. Make sure:
1. You're not on main branch
2. Your changes are committed
3. origin/main is up to date: `git fetch origin main`

### Pipeline always runs everything
Check if you're on main branch:
```bash
echo $BUILDKITE_BRANCH
```
On main, the pipeline runs more conservatively (all tests).

### Script syntax errors
Make sure the script is executable:
```bash
chmod +x .buildkite/generate-pipeline.sh
```

## Future Enhancements

Possible improvements:
- [ ] Add test selection based on code coverage
- [ ] Integrate with GitHub PR labels (e.g., `skip-tests`)
- [ ] Add dependency graph analysis (only test affected modules)
- [ ] Cache test results across similar changes
- [ ] Smart Docker layer caching

## Resources

- [Buildkite Dynamic Pipelines](https://buildkite.com/docs/pipelines/defining-steps#dynamic-pipelines)
- [Pipeline Upload Command](https://buildkite.com/docs/agent/v3/cli-pipeline#uploading-pipelines)
- [Build Step Conditionals](https://buildkite.com/docs/pipelines/conditionals)
