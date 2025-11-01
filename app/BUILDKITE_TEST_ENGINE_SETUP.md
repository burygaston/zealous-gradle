# Buildkite Test Engine Client (bktec) Setup

This project uses Buildkite Test Engine Client (bktec) for advanced test analytics and intelligent test splitting for JavaScript tests.

## Prerequisites

You need a Buildkite Test Engine API token with the following scopes:
- `read_suites`
- `read_test_plan`
- `write_test_plan`

## Step 1: Generate API Token

1. Go to Buildkite → **Test Engine** → **Settings**
2. Click on **API Tokens**
3. Click **"New API Token"**
4. Give it a name: `Test Engine API Token`
5. Select the required scopes:
   - ✅ `read_suites`
   - ✅ `read_test_plan`
   - ✅ `write_test_plan`
6. Click **"Create Token"**
7. **Copy the token** (you'll only see it once!)

## Step 2: Add Token to Buildkite Pipeline

**IMPORTANT**: Never commit the token to your repository. Buildkite will automatically revoke exposed tokens.

### Option A: Pipeline-Level Environment Variable (Recommended)

1. Go to your Buildkite pipeline → **Settings**
2. Click on **Environment Variables** (under "Build Configuration")
3. Click **"Add Environment Variable"**
4. Set:
   - **Name**: `BUILDKITE_TEST_ENGINE_API_ACCESS_TOKEN`
   - **Value**: Paste your API token
5. Click **"Add Environment Variable"**

### Option B: Organization-Level Environment Variable

1. Go to Buildkite → **Organization Settings**
2. Click on **Environment Variables**
3. Add the same variable as above
4. This makes it available to all pipelines in your organization

## Step 3: Create Test Suites

1. Go to Buildkite → **Test Engine**
2. Click **"New Suite"**
3. Create two suites:
   - **Suite Name**: `javascript-tests-node18`
   - **Suite Slug**: `javascript-tests-node18`

   - **Suite Name**: `javascript-tests-node20`
   - **Suite Slug**: `javascript-tests-node20`

## Step 4: Verify Setup

Run your pipeline. You should see:

```
Installing Buildkite Test Engine Client (bktec)
bktec version: vX.X.X

Running Jest tests with Node 18 via bktec
✅ Test Engine token configured - running with bktec
Suite: javascript-tests-node18
```

If the token is missing, you'll see:
```
⚠️  BUILDKITE_TEST_ENGINE_API_ACCESS_TOKEN not set - running tests without bktec
```

## Step 5: Enable Test Splitting (Optional)

To enable parallel test execution with intelligent splitting:

1. Update the JavaScript test step in `.buildkite/pipeline.yml`:
   ```yaml
   - label: ":jest: Run JavaScript Tests (Hosted) - Node {{matrix.node_version}}"
     parallelism: 5  # Add this line - split across 5 agents
     # ... rest of configuration
   ```

2. bktec will automatically:
   - Split tests across 5 agents
   - Balance by historical timing data
   - Retry only flaky tests
   - Report results to Test Engine

## Viewing Test Analytics

1. Go to Buildkite → **Test Engine**
2. Select your suite (e.g., `javascript-tests-node18`)
3. View:
   - **Flaky test detection**
   - **Test timing trends**
   - **Failure patterns**
   - **Test duration analysis**

## Troubleshooting

### Token Revoked
If you accidentally commit the token:
1. Buildkite will automatically revoke it
2. Generate a new token (Step 1)
3. Update the environment variable (Step 2)

### bktec Not Running Tests
Check the logs for:
```
⚠️  BUILDKITE_TEST_ENGINE_API_ACCESS_TOKEN not set
```
If you see this, the token isn't properly configured in Buildkite settings.

### Suite Not Found
Ensure the suite slug matches exactly:
- Environment variable: `BUILDKITE_TEST_ENGINE_SUITE_SLUG: "javascript-tests-node18"`
- Test Engine suite slug: `javascript-tests-node18`

## Configuration Reference

The following environment variables are pre-configured in the pipeline:

```yaml
env:
  BUILDKITE_TEST_ENGINE_SUITE_SLUG: "javascript-tests-node{{matrix.node_version}}"
  BUILDKITE_TEST_ENGINE_TEST_RUNNER: "jest"
  BUILDKITE_TEST_ENGINE_RESULT_PATH: "test-results/bktec-result.json"
```

The API token **must be set in Buildkite UI**, not in the pipeline.yml file.

## Benefits

With bktec enabled, you get:
- ✅ **Flaky test detection** - Automatically identifies unreliable tests
- ✅ **Test timing analytics** - See which tests are slowest
- ✅ **Intelligent splitting** - Distribute tests optimally across agents
- ✅ **Historical trends** - Track test performance over time
- ✅ **Failure analysis** - Identify patterns in test failures

## Resources

- [bktec GitHub Repository](https://github.com/buildkite/test-engine-client)
- [Buildkite Test Engine Documentation](https://buildkite.com/docs/test-engine)
- [Test Splitting Guide](https://buildkite.com/docs/test-engine/test-splitting)
