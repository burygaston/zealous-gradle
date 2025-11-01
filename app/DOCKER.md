# Docker Setup

This document explains how to build and run the Task Manager application using Docker.

## Building the Docker Image Locally

```bash
# Build the Docker image
docker build -t task-manager:latest .

# Build with a specific tag
docker build -t task-manager:1.0.0 .
```

## Running the Container

### Quick Start (H2 in-memory database)

```bash
# Run with H2 database (no external dependencies)
docker run -p 8080:8080 task-manager:latest

# Access the application at http://localhost:8080
# Login with: demo / password
```

### Run with MySQL Database

```bash
# Start MySQL container first
docker run -d \
  --name task-manager-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=taskmanager \
  -e MYSQL_USER=taskmanager \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  mysql:8.0

# Run the application with MySQL profile
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=default \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/taskmanager \
  -e SPRING_DATASOURCE_USERNAME=taskmanager \
  -e SPRING_DATASOURCE_PASSWORD=password \
  task-manager:latest
```

### Using Docker Compose

```bash
# Start both MySQL and the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

## Pulling from DockerHub

If the image is published to DockerHub via Buildkite:

```bash
# Pull the latest image
docker pull ${DOCKERHUB_USERNAME}/task-manager:latest

# Pull a specific version
docker pull ${DOCKERHUB_USERNAME}/task-manager:123-abc1234

# Run the pulled image
docker run -p 8080:8080 ${DOCKERHUB_USERNAME}/task-manager:latest
```

## Environment Variables

The following environment variables can be configured:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (h2 or default) | `h2` |
| `SPRING_DATASOURCE_URL` | Database JDBC URL | - |
| `SPRING_DATASOURCE_USERNAME` | Database username | - |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `SERVER_PORT` | Application port | `8080` |

## Buildkite CI/CD

The Buildkite pipeline automatically:

1. Builds the Docker image
2. Tags it with `${BUILD_NUMBER}-${COMMIT_SHA}` and `latest`
3. Pushes to DockerHub

### Required Environment Variables in Buildkite

Set these in your Buildkite pipeline settings:

- `DOCKERHUB_USERNAME`: Your DockerHub username
- `DOCKERHUB_TOKEN`: Your DockerHub access token (create at https://hub.docker.com/settings/security)

### Agent Configuration

The Docker build step requires an agent with:
- Docker installed and running
- Access to DockerHub
- Queue tag: `docker`

## Multi-Stage Build

The Dockerfile uses a multi-stage build:

1. **Build Stage**: Uses `gradle:8.5-jdk17` to compile the application
2. **Runtime Stage**: Uses `eclipse-temurin:17-jre-alpine` for a smaller final image

Benefits:
- Smaller final image size (~200MB vs ~800MB)
- No build tools in production image
- Faster deployment and startup
- Better security (minimal attack surface)

## Security Features

- Runs as non-root user (`spring:spring`)
- Uses Alpine-based JRE for minimal footprint
- Only exposes necessary port (8080)
- No unnecessary build tools in final image

## Health Check

The container includes a health check that pings `/actuator/health` every 30 seconds.

Check container health:
```bash
docker ps
docker inspect --format='{{.State.Health.Status}}' <container-id>
```

## Troubleshooting

### Container won't start

```bash
# Check logs
docker logs <container-id>

# Run interactively to debug
docker run -it --entrypoint /bin/sh task-manager:latest
```

### Database connection issues

```bash
# Verify network connectivity
docker exec -it <container-id> ping host.docker.internal

# Check environment variables
docker exec -it <container-id> env | grep SPRING
```

### Image size too large

The multi-stage build should result in ~200MB images. If larger:
- Check `.dockerignore` is properly excluding build artifacts
- Verify using Alpine-based JRE image
- Consider using `docker build --squash` (experimental)
