# Task Manager

A comprehensive Spring Boot-based task management application with a modern timeline UI, combining features from Asana and Notion.

## Features

- **User Management**: Secure authentication with Spring Security and BCrypt password hashing
- **Work Items**: Full CRUD operations for tasks with status tracking (TODO, IN_PROGRESS, COMPLETE)
- **Labels**: Color-coded labels with icons for visual organization
- **Timeline UI**: Modern dark theme with glassmorphism design and vertical timeline view
- **Reports**: Comprehensive reporting with counts by status and overdue analysis
- **Email Scheduling**: Daily and weekly automated reports via cron scheduling
- **REST API**: Complete RESTful API for all operations

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Java**: 17
- **Build Tool**: Gradle 8.5
- **Database**: MySQL 8.0
- **Security**: Spring Security with BCrypt
- **Frontend**: Thymeleaf + vanilla JavaScript
- **Testing**: JUnit 5, Mockito
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 17 or higher
- Docker & Docker Compose (for MySQL)
- Gradle 8.5 (or use included wrapper)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd task-manager
```

### 2. Start MySQL with Docker Compose

```bash
docker-compose up -d mysql
```

This will:
- Start MySQL 8.0 container
- Create the `taskmanager` database
- Initialize schema and demo data
- Expose MySQL on port 3306

### 3. Run the Application

Using Gradle wrapper:

```bash
./gradlew bootRun
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/task-manager-1.0.0.jar
```

### 4. Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

**Demo Credentials:**
- Username: `demo`
- Password: `password`

## Database Schema

The application uses the following tables:

- **users**: User accounts with authentication
- **work_items**: Tasks/work items with status, deadline, priority
- **labels**: Reusable labels with colors and icons
- **work_item_labels**: Many-to-many junction table

All tables include proper indexes for optimal query performance.

## API Endpoints

### Work Items

- `GET /api/workitems` - Get all work items (sorted by deadline)
- `GET /api/workitems/{id}` - Get specific work item
- `GET /api/workitems/status/{status}` - Filter by status
- `GET /api/workitems/overdue` - Get overdue items
- `POST /api/workitems` - Create new work item
- `PUT /api/workitems/{id}` - Update work item
- `DELETE /api/workitems/{id}` - Delete work item

### Labels

- `GET /api/labels` - Get all labels
- `GET /api/labels/{id}` - Get specific label
- `POST /api/labels` - Create new label
- `PUT /api/labels/{id}` - Update label
- `DELETE /api/labels/{id}` - Delete label

### Reports

- `GET /api/reports` - Generate comprehensive report

## Configuration

### Database Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskmanager
    username: taskuser
    password: taskpass123
```

### Email Configuration

For email notifications, set environment variables:

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

Or configure in `application.yml`:

```yaml
spring:
  mail:
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

### Scheduler Configuration

Email reports are scheduled via cron expressions:

- **Daily Report**: `0 0 18 * * ?` (6 PM daily)
- **Weekly Report**: `0 0 18 ? * FRI` (6 PM every Friday)

Customize in `application.yml`:

```yaml
app:
  scheduler:
    daily-report-cron: "0 0 18 * * ?"
    weekly-report-cron: "0 0 18 ? * FRI"
```

## Development

### Running Tests

Run all tests:

```bash
./gradlew test
```

Run specific test class:

```bash
./gradlew test --tests WorkItemServiceTest
```

### Code Quality

Run code quality checks:

```bash
# Checkstyle
./gradlew checkstyleMain checkstyleTest

# PMD
./gradlew pmdMain pmdTest

# SpotBugs
./gradlew spotbugsMain spotbugsTest

# All quality checks
./gradlew check
```

### Building

Build the project:

```bash
./gradlew clean build
```

Build without tests:

```bash
./gradlew clean build -x test
```

Create distribution artifacts:

```bash
./gradlew prepareArtifacts
```

## Docker Deployment

### Build and Run with Docker Compose

```bash
docker-compose up --build
```

This will:
1. Build the application Docker image
2. Start MySQL container
3. Start the application container
4. Expose application on port 8080

### Stop Services

```bash
docker-compose down
```

Remove volumes:

```bash
docker-compose down -v
```

## CI/CD with Buildkite

The project includes a comprehensive Buildkite pipeline (`.buildkite/pipeline.yml`) featuring:

- **Parallel test execution** across 3 shards
- **Artifact management** (upload/download/verification)
- **Docker Compose integration** for integration tests
- **Flaky test detection** with 3 iterations
- **Code quality analysis** (Checkstyle, PMD, SpotBugs)
- **Build caching** with Docker volumes
- **Test splitting** with Buildkite Test Engine
- **Automatic retries** for failed tests
- **Build annotations** and summary reports
- **Deployment gate** with manual approval

### Pipeline Steps

1. Setup & Cache Dependencies
2. Build Application (with JAR artifact upload)
3. Unit Tests (parallel shards with test-collector)
4. Integration Tests (Docker Compose)
5. Flaky Test Detection
6. Code Quality Checks (parallel)
7. Package Artifacts
8. Download & Verify Artifacts
9. Build Summary with Annotations
10. Deploy Gate (manual approval)
11. Deploy to Staging

### Required Environment Variables

```bash
BUILDKITE_TEST_ENGINE_API_KEY=<your-key>
MAIL_USERNAME=<email>
MAIL_PASSWORD=<password>
```

## Project Structure

```
task-manager/
├── .buildkite/
│   └── pipeline.yml
├── config/
│   ├── checkstyle/
│   └── pmd/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── js/
│   │       ├── templates/
│   │       ├── application.yml
│   │       └── schema.sql
│   └── test/
│       └── java/com/taskmanager/service/
├── docker-compose.yml
├── Dockerfile
├── build.gradle
└── README.md
```

## Features Highlight

### Timeline View

The application features a creative vertical timeline UI (not swimlanes) with:

- Tasks displayed as cards along a gradient timeline
- Color-coded borders based on status:
  - **Yellow**: TODO
  - **Blue**: IN_PROGRESS
  - **Green**: COMPLETE
  - **Red**: OVERDUE (with pulsing animation)
- Sorted by deadline (most urgent at top)
- Glassmorphism design with dark theme
- Hover effects and smooth transitions

### Status Tracking

Work items automatically track:
- Creation timestamp
- Last update timestamp
- Completion timestamp (set when status changes to COMPLETE)
- Overdue detection based on deadline

### Security

- BCrypt password hashing (strength 10)
- Form-based authentication
- CSRF protection
- Session management
- Secured endpoints (all require authentication)

## Troubleshooting

### Port Already in Use

If port 8080 or 3306 is already in use:

```bash
# Change application port in application.yml
server:
  port: 8081

# Or set environment variable
export SERVER_PORT=8081
```

### MySQL Connection Issues

Check if MySQL is running:

```bash
docker-compose ps
```

View MySQL logs:

```bash
docker-compose logs mysql
```

### Email Not Sending

Ensure:
1. SMTP credentials are configured correctly
2. Gmail app password is used (not regular password)
3. Less secure app access is enabled (or use OAuth2)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For issues and questions:
- Open an issue on GitHub
- Contact: support@taskmanager.example.com

## Acknowledgments

- Spring Boot team for the excellent framework
- Buildkite for CI/CD platform
- All contributors to the open-source libraries used in this project