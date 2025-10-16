# Buildkite Agent Setup Guide

This guide explains how to set up a Buildkite agent (hosted or self-hosted) to run the Task Manager project's CI/CD pipeline.

## Overview

The project requires the following tools to run successfully:
- **Java 21** - For building and testing the Spring Boot application
- **Gradle 8.5** - Build automation (uses wrapper, so not strictly required)
- **Node.js 20.x** - For running JavaScript/Jest tests
- **Docker CLI** - For building and pushing Docker images
- **GitHub CLI** (optional) - For PR operations

## Option 1: Buildkite Hosted Agents (Recommended)

Buildkite hosted agents require a custom Docker image. We provide two Dockerfiles:

### Slim Image (Recommended)
**File**: `Dockerfile.buildkite-agent-slim`

This image is built on top of the official Buildkite hosted agent base image:
- **Base**: `buildkite/hosted-agent-base:ubuntu-v1.0.1` (includes git, docker, curl, wget, etc.)
- **Added**: Amazon Corretto Java 21
- **Added**: Node.js 20.x
- **Configured**: Optimized Gradle settings for CI

**Build and push the image:**

```bash
# Build the image
docker build -f Dockerfile.buildkite-agent-slim -t your-registry/task-manager-buildkite-agent:latest .

# Push to your container registry
docker push your-registry/task-manager-buildkite-agent:latest
```

**Configure in Buildkite:**

1. Go to your Buildkite organization settings
2. Navigate to **Hosted Agents** → **Image Management**
3. Add a new image:
   - **Image Name**: `your-registry/task-manager-buildkite-agent:latest`
   - **Architecture**: `amd64`
4. Create a new queue using this image

### Full Image (Alternative)
**File**: `Dockerfile.buildkite-agent`

Based on the official `buildkite/agent:3` image with all tools pre-installed.
Larger but includes the Buildkite agent binary.

## Option 2: Self-Hosted Agents

If you're running your own Buildkite agents, ensure they have these installed:

### Ubuntu/Debian

```bash
# Install Java 21
wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -
sudo add-apt-repository 'deb https://apt.corretto.aws stable main'
sudo apt-get update
sudo apt-get install -y java-21-amazon-corretto-jdk

# Install Node.js 20.x
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo bash -
sudo apt-get install -y nodejs

# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker buildkite-agent

# Install GitHub CLI (optional)
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | \
  sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] \
  https://cli.github.com/packages stable main" | \
  sudo tee /etc/apt/sources.list.d/github-cli.list
sudo apt-get update
sudo apt-get install gh
```

### macOS

```bash
# Install via Homebrew
brew install openjdk@21 node@20 docker gh

# Link Java
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

## Environment Variables

The following environment variables should be set in your Buildkite pipeline or agent configuration:

### Required
```bash
BUILDKITE_ANALYTICS_TOKEN=VXzdR2LfZPQ93FXXetcCcsJW  # For Test Suite integration
```

### Optional (for Docker step)
```bash
DOCKERHUB_USERNAME=your-dockerhub-username
DOCKERHUB_TOKEN=your-dockerhub-token
```

## Pipeline Configuration

The `.buildkite/pipeline.yml` is already configured to work with the agent. Key features:

- **Build step**: Compiles Java code with Gradle
- **Java tests**: Runs 120 JUnit tests (including 10 flaky tests)
- **JavaScript tests**: Runs 110 Jest tests (including 10 flaky tests)
- **Test Suite integration**: Both Java and JS results sent to Buildkite Test Suite
- **Test annotations**: Automatic summary of test results
- **Docker build**: Creates and pushes Docker image
- **Soft fail**: Tests can fail but pipeline continues for visibility

## Testing the Agent

Build and test the Docker image locally:

```bash
# Build the slim image
docker build -f Dockerfile.buildkite-agent-slim -t task-manager-agent .

# Test the image
docker run --rm task-manager-agent java -version
docker run --rm task-manager-agent node --version
docker run --rm task-manager-agent npm --version
docker run --rm task-manager-agent docker --version
docker run --rm task-manager-agent gh --version

# Run a test build
docker run --rm -v $(pwd):/workspace -w /workspace task-manager-agent bash -c "
  ./gradlew clean build -x test
  npm install
  npm test
"
```

## Image Sizes

- **Slim image**: ~800 MB (builds on Buildkite base image + Java + Node.js)
- **Full image**: ~1.5 GB (includes Buildkite agent binary)

## Troubleshooting

### Java version mismatch
Ensure Java 21 is installed. Check with:
```bash
java -version
```

### Node.js version too old
Ensure Node.js 20.x or newer:
```bash
node --version
```

### Gradle daemon issues
The images are pre-configured to disable the Gradle daemon for CI:
```properties
org.gradle.daemon=false
org.gradle.parallel=true
```

### Docker permission denied
For self-hosted agents, ensure the buildkite-agent user is in the docker group:
```bash
sudo usermod -aG docker buildkite-agent
```

### Test failures
The project includes intentional flaky tests (25-30% failure rate) to demonstrate Buildkite Test Suite's flaky test detection. This is expected behavior.

## Support

For issues with:
- **Buildkite Hosted Agents**: Contact Buildkite support
- **This project**: Open an issue in the GitHub repository
- **Docker images**: Check Docker build logs and verify all dependencies are installed

## Additional Resources

- [Buildkite Hosted Agents Documentation](https://buildkite.com/docs/agent/hosted)
- [Buildkite Test Suite](https://buildkite.com/docs/test-analytics)
- [Docker Documentation](https://docs.docker.com/)
