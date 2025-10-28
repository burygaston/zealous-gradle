# Buildkite CI/CD Demo Script (5-10 minutes)

## Demo Overview
**Project**: Task Manager - Spring Boot application with Java and JavaScript tests
**Goal**: Demonstrate Buildkite's powerful CI/CD features including matrix builds, parallelism, and dynamic pipelines

---

## PART 1: Introduction (1 minute)

### Talking Points:
"Today I'm going to show you how we've set up a modern CI/CD pipeline using Buildkite for our Task Manager application. We'll cover:
- Matrix builds for testing across multiple versions
- Parallel test execution for speed
- Dynamic pipelines that intelligently select which tests to run
- Test analytics with Buildkite Test Suite"

### Show:
- **GitHub repo**: `zealous-gradle` - Spring Boot + JavaScript project
- **Buildkite dashboard**: Show the pipeline overview

---

## PART 2: Basic Pipeline & Matrix Builds (2 minutes)

### Talking Points:
"Let's start with our standard pipeline. We need to test our Java code across multiple Java versions to ensure compatibility."

### Show in UI:
1. **Navigate to**: Main branch build
2. **Point out**: The pipeline structure in `.buildkite/pipeline.yml`

### Demo Actions:
```
Show the pipeline.yml file and highlight:
```

**Key Feature: Matrix Builds**
```yaml
matrix:
  setup:
    java_version:
      - "17"
      - "21"
```

### Talking Points:
"With matrix builds, we automatically run tests on both Java 17 and Java 21. Buildkite creates separate jobs for each combination - in this case 2 jobs from a single step definition."

### Show in Build:
- Point to the two parallel Java test jobs
- Show how they run simultaneously (or sequentially with 1 agent)
- Click into one to show the logs showing "Running Java tests with version: 17"

---

## PART 3: Parallelism for Speed (2 minutes)

### Talking Points:
"For our JavaScript tests, we use parallelism to split the test suite across multiple agents for faster execution."

**Key Feature: Parallelism**
```yaml
- label: "JavaScript Tests - Node 18"
  parallelism: 4
```

### Show in Build:
1. Navigate to JavaScript test step
2. **Point out**: 4 parallel jobs (1/4, 2/4, 3/4, 4/4)
3. **Explain**: "Buildkite Test Engine (bktec) would automatically distribute tests across these 4 agents intelligently"

### Talking Points:
"Without parallelism, our JS tests might take 8 minutes. With 4 parallel agents, we cut that down to ~2 minutes. That's a 4x speedup!"

---

## PART 4: Test Analytics (1 minute)

### Talking Points:
"We're using Buildkite Test Suite to collect test results and identify flaky tests."

### Show in UI:
1. **Navigate to**: Test Suite dashboard (if available)
2. **Point out**: Test-collector plugin in pipeline.yml:

```yaml
plugins:
  - test-collector#v1.10.2:
      files: "build/test-results/test/*.xml"
      format: "junit"
```

### Talking Points:
"This gives us:
- Test execution trends over time
- Flaky test detection
- Test duration analytics
- Failed test history"

---

## PART 5: Dynamic Pipelines - The Star Feature (3-4 minutes)

### Talking Points:
"Now for the exciting part - dynamic pipelines. Why run all tests when only Java code changed? Let's make our pipeline intelligent."

### Demo Setup:
1. **Navigate to**: `feature/dynamic-pipeline-demo` branch
2. **Show**: The pipeline structure

### Show File Structure:
```
.buildkite/
├── pipeline-dynamic.yml          # Entry point
├── generate-pipeline.sh          # Intelligence
└── DYNAMIC_PIPELINE_README.md    # Documentation
```

### Walk Through the Flow:

**Step 1: Show pipeline-dynamic.yml**
```yaml
steps:
  - label: ":wave: Hello World"
    command: echo "Hello! This runs BEFORE the dynamic pipeline generator"

  - label: ":pipeline: Generate Dynamic Pipeline"
    command: |
      chmod +x .buildkite/generate-pipeline.sh
      .buildkite/generate-pipeline.sh | buildkite-agent pipeline upload
```

### Talking Points:
"The entry pipeline has just 2 steps:
1. A hello world (to demonstrate static steps)
2. A generator step that analyzes what changed and dynamically creates more steps"

**Step 2: Show generate-pipeline.sh logic**
```bash
# Detect what changed
JAVA_CHANGED=$(echo "$CHANGED_FILES" | grep -c '\.java$' || echo "0")
JS_CHANGED=$(echo "$CHANGED_FILES" | grep -c '\.js$\|\.jsx$\|\.ts$\|\.tsx$' || echo "0")

# Only generate Java tests if Java changed
if [[ $JAVA_CHANGED -gt 0 ]]; then
  # Generate Java test matrix
else
  # Skip Java tests
fi
```

### Talking Points:
"The script:
1. Compares current branch to main
2. Detects which files changed (Java, JavaScript, Docker, etc.)
3. Generates YAML for only the relevant tests
4. Uploads the generated pipeline to the current build"

**Step 3: Show a Build in Action**

Trigger a build and show:

1. **Hello World** step completes
2. **Generate Dynamic Pipeline** step shows:
   ```
   --- :mag: Analyzing changes to generate optimal pipeline
   Changed files since origin/main:
     - src/main/java/com/taskmanager/controller/WorkItemController.java

   Component change detection:
     Java files: 1
     JavaScript files: 0
   ```

3. **New steps appear dynamically**:
   - Build Project
   - Run Java Tests - Java 17 ✅
   - Run Java Tests - Java 21 ✅
   - JavaScript Tests (skipped - no JS changes) ⏭️
   - Test Results Summary

### Talking Points:
"Notice the JavaScript test step says 'skipped - no JS changes'. We saved:
- 8 parallel jobs (Node 18 x 4 + Node 20 x 4)
- ~5 minutes of build time
- CI/CD resource costs

This is especially powerful for large teams with microservices!"

---

## PART 6: Benefits & Use Cases (1 minute)

### Talking Points:
"Let's recap the benefits we demonstrated:

**Matrix Builds:**
- Test across multiple Java/Node versions
- Single step definition → multiple jobs
- Ensure compatibility

**Parallelism:**
- Split tests across agents
- 4x speedup in our case
- Configurable based on test suite size

**Dynamic Pipelines:**
- Only run relevant tests
- 50-80% faster CI for small changes
- Reduces queue time and cost

**Test Analytics:**
- Identify flaky tests
- Track test performance over time
- Data-driven CI optimization"

### Real-World Scenarios:

**Scenario 1: Documentation-only change**
```
Changed: README.md
Result: Build only, skip all tests (~2 minutes vs ~15 minutes)
```

**Scenario 2: Java-only change**
```
Changed: WorkItemController.java
Result: Build + Java tests only (~8 minutes vs ~15 minutes)
```

**Scenario 3: Main branch (deploy)**
```
Branch: main
Result: Full pipeline + Docker build + deployment
```

---

## PART 7: Advanced Features Mentioned (30 seconds)

### Quick Mentions:
"Additional features we explored but didn't demo today:
- **Buildkite Clusters**: Organize agents, queues, and secrets
- **Queue routing**: Direct jobs to specific agent types (hosted vs self-hosted)
- **Buildkite Test Engine (bktec)**: Intelligent test splitting based on timing data
- **Artifacts**: Share build outputs between steps
- **Docker caching**: Speed up container builds"

---

## CLOSING (30 seconds)

### Talking Points:
"To summarize, we've built a sophisticated CI/CD pipeline that:
1. ✅ Tests across multiple versions (Matrix)
2. ✅ Runs tests in parallel for speed
3. ✅ Intelligently selects which tests to run (Dynamic pipelines)
4. ✅ Provides test analytics for continuous improvement

The result? Faster feedback for developers, lower CI costs, and more reliable software."

### Show:
- **Final build**: Point to total build time and resource usage
- **Compare**: Show a main branch build (15 min) vs feature branch (8 min)

---

## Demo Preparation Checklist

### Before the Demo:
- [ ] Ensure local agent is running
- [ ] Have 2 browser tabs open:
  - Tab 1: GitHub repo
  - Tab 2: Buildkite pipeline
- [ ] Pre-run a build on main branch (for comparison)
- [ ] Pre-run a build on feature/dynamic-pipeline-demo
- [ ] Have `.buildkite/generate-pipeline.sh` open in editor
- [ ] Test connectivity (agents showing as connected)

### Files to Have Open:
1. `.buildkite/pipeline.yml` - static pipeline
2. `.buildkite/pipeline-dynamic.yml` - entry point
3. `.buildkite/generate-pipeline.sh` - generator script
4. `src/main/java/com/taskmanager/controller/WorkItemController.java` - the change

### Key Talking Points to Remember:
1. "Matrix builds → test multiple versions from one step"
2. "Parallelism → split tests across agents for speed"
3. "Dynamic pipelines → only run what changed"
4. "Test Suite → identify flaky tests and trends"

---

## Q&A Preparation

### Common Questions:

**Q: How does it know which tests to run?**
A: The generator script compares the current branch to main using `git diff`, detects file types (.java, .js, etc.), and generates pipeline steps conditionally.

**Q: What if I want to force all tests?**
A: You can either:
- Manually trigger on main branch (always runs everything)
- Touch a build.gradle file to force Java tests
- Modify the generator logic to add a "force full pipeline" label check

**Q: Can this work with other CI systems?**
A: Dynamic pipelines are Buildkite-specific, but GitLab and GitHub Actions have similar concepts (rules/conditions). The principle of intelligent test selection applies everywhere.

**Q: How much time/cost savings?**
A: In our demo:
- Documentation changes: 87% faster (2 min vs 15 min)
- Java-only changes: 47% faster (8 min vs 15 min)
- For a team of 20 developers making 5 commits/day, this saves ~10 hours of CI time daily

**Q: Does this work with Docker builds?**
A: Yes! The dynamic pipeline includes Docker build steps only on main branch or when Dockerfile changes.

---

## Timing Breakdown (Total: 9-10 minutes)

| Section | Time | Key Action |
|---------|------|------------|
| Intro | 1 min | Set context |
| Matrix Builds | 2 min | Show Java 17/21 parallel jobs |
| Parallelism | 2 min | Show JS tests split 4 ways |
| Test Analytics | 1 min | Show test-collector plugin |
| Dynamic Pipelines | 3-4 min | Walk through generator + live build |
| Benefits | 1 min | Recap value proposition |
| Advanced Features | 0.5 min | Quick mentions |
| Closing | 0.5 min | Summary |

---

## Success Metrics for Demo

By the end, audience should understand:
- ✅ What matrix builds are and when to use them
- ✅ How parallelism speeds up test execution
- ✅ How dynamic pipelines reduce unnecessary work
- ✅ The ROI of intelligent CI/CD (time + cost savings)

## Post-Demo Resources

Share these with the audience:
- `.buildkite/DYNAMIC_PIPELINE_README.md` - Full documentation
- Buildkite docs: https://buildkite.com/docs/pipelines/defining-steps
- This demo repo: https://github.com/burygaston/zealous-gradle

---

**Good luck with your demo! 🚀**
