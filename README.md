# Research Conference Registration & Session Management System (RCRSMS)

> A robust, enterprise-grade Spring Boot application designed to streamline conference registration workflows and session management with secure authentication and role-based access control.

---

## System Overview

The **Research Conference Registration & Session Management System** is a comprehensive web application built on the **Spring Boot MVC architectural pattern**, leveraging the power of **Hibernate ORM** for seamless object-relational mapping. The system automates complex conference operations including user authentication, session scheduling, participant registration, and attendance tracking.

The application implements:

- **Automated ORM Layer**: Hibernate automatically handles object persistence and relationship management through entity annotations
- **Property-Driven Configuration**: All database and application settings are managed via `application.properties`, enabling environment-specific deployments without code changes
- **Multi-Role Access Control**: Role-based authentication (Admin, Chair, Participant) with Spring Security integration
- **RESTful MVC Controllers**: Clean separation of concerns with Thymeleaf template rendering and dynamic web interfaces

---

## Tech Stack

| Component               | Technology                     | Version              |
| ----------------------- | ------------------------------ | -------------------- |
| **Language**            | Java                           | 23                   |
| **Framework**           | Spring Boot                    | 3.5.7                |
| **Build Tool**          | Gradle                         | Latest (via wrapper) |
| **ORM Framework**       | Hibernate / Spring Data JPA    | 3.5.7                |
| **Validation**          | Jakarta Validation Constraints | Latest               |
| **Database**            | MySQL                          | 8.0.33+              |
| **Database Migrations** | Flyway                         | 11.17.0              |
| **Security**            | Spring Security                | 3.5.7                |
| **Authentication**      | JWT (JJWT)                     | 0.13.0               |
| **Template Engine**     | Thymeleaf                      | 3.5.7                |
| **Testing**             | JUnit 5                        | Latest               |

---

## Core Implementation Highlights

### 1. Database Constraint-Driven Validation

**Jakarta Validation Constraints** work in tandem with **database-level constraints** to provide multi-layered validation:

#### Conference & Session Title Validation

```java
@NotBlank(message = "Title is required")
@Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
private String title;
```

- **Application Layer**: Jakarta `@Size` constraint prevents invalid inputs before persistence
- **Database Layer**: Column definition automatically enforces character limits
- **Benefit**: Invalid data cannot enter the system even if application validation is bypassed

#### Conference Date Validation

```sql
ALTER TABLE conferences
ADD CONSTRAINT chk_conference_date
    CHECK (end_date >= start_date);
```

- Ensures end dates logically occur after start dates
- Database-level enforcement prevents inconsistent data

### 2. Duplicate Registration Prevention

The system prevents users from registering for the same session multiple times using a **dual-layer approach**:

#### Database Unique Constraint

```java
@Table(name = "registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "session_id"})
})
```

```sql
ALTER TABLE registrations
ADD CONSTRAINT uq_user_session
    UNIQUE (user_id, session_id);
```

#### Repository Query Method

```java
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByUser_userIdAndSession_sessionId(Long userId, Long sessionId);
}
```

**Implementation Pattern**:

```java
if (registrationRepository.existsByUser_userIdAndSession_sessionId(userId, sessionId)) {
    throw new DuplicateRegistrationException("User already registered for this session");
}
```

**Benefits**:

- **Optimized Query Performance**: Single derived query method leveraging Hibernate's query generation
- **Data Integrity**: Database unique constraint prevents race conditions
- **Business Logic Protection**: Application layer checks provide immediate user feedback

### 3. Hibernate ORM & Entity Relationships

All domain entities are managed through **Hibernate's automatic relationship management**:

- **@ManyToOne Relationships**: Efficient lazy loading with customizable fetch strategies
- **Cascade Operations**: Automatic orphan removal prevents orphaned records in the database
- **Cascade Deletes**: Deletion of parent entities cascades to child entities when configured

Example from Registration entity:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", nullable = false)
private UserEntity user;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "session_id", nullable = false)
private Session session;
```

---

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         ENTITY RELATIONSHIPS                     │
└─────────────────────────────────────────────────────────────────┘

        ┌──────────────────┐
        │   UserEntity     │
        │   (users table)  │
        ├──────────────────┤
        │ - userId (PK)    │
        │ - username       │
        │ - password       │
        │ - role           │
        └────────┬─────────┘
                 │
                 │ 1:M (One User : Many Registrations)
                 │
                 ▼
        ┌──────────────────────┐
        │   Registration       │◄─────── JUNCTION/BRIDGE ENTITY
        │ (registrations tbl)  │         Links User → Session
        ├──────────────────────┤
        │ - registrationId(PK) │
        │ - user_id (FK)   ────┼─────► References UserEntity
        │ - session_id (FK)────┼─────► References Session
        │ - registrationDate   │
        │ - attended           │
        └──────────┬───────────┘
                   │
                   │ M:1 (Many Registrations : One Session)
                   │
                   ▼
        ┌──────────────────┐
        │    Session       │
        │  (sessions tbl)  │
        ├──────────────────┤
        │ - sessionId (PK) │
        │ - title          │
        │ - status         │
        │ - sessionTime    │
        │ - chair_id (FK)  │◄──────── References UserEntity
        │ - room_id (FK)   │◄──────── References Room
        │ - conference_id  │◄──────── References Conference
        └──────────────────┘
                   △
                   │
                   │ M:1
                   │
        ┌──────────────────┐
        │   Conference     │
        │(conferences tbl) │
        ├──────────────────┤
        │ - conferenceId   │
        │ - name           │
        │ - location       │
        │ - startDate      │
        │ - endDate        │
        └──────────────────┘


▓▓▓ KEY DESIGN PATTERN ▓▓▓

Registration serves as a BRIDGE ENTITY:
• Resolves M:M (Many-to-Many) relationship between Users and Sessions
• Tracks additional metadata: registrationDate, attended status
• Implements unique constraint preventing duplicate user-session pairs
• Provides rich query interface through JpaRepository derived methods
```

---

## Local Setup Instructions

### Prerequisites

Ensure you have the following installed on your system:

- **Java Development Kit (JDK) 23+**: [Download from Oracle](https://www.oracle.com/java/technologies/downloads/)
- **MySQL Server 8.0+**: [Download from MySQL](https://dev.mysql.com/downloads/mysql/)
- **Git**: For version control

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/SE-Registration-and-Session-Management-System.git
cd SE-Registration-and-Session-Management-System
```

### Step 2: Configure Database Connection

Create or update the `application.properties` file at `src/main/resources/application.properties`:

```properties
# Spring Boot Application
spring.application.name=RCRSMS

# ========== DATABASE CONFIGURATION ==========
spring.datasource.url=jdbc:mysql://localhost:3306/rcrsms_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ========== HIBERNATE CONFIGURATION ==========
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# ========== FLYWAY CONFIGURATION ==========
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migrations
spring.flyway.baselineOnMigrate=true

# ========== SERVER CONFIGURATION ==========
server.port=8080
server.servlet.context-path=/

# ========== LOGGING CONFIGURATION ==========
logging.level.root=INFO
logging.level.com.project5.rcrsms=DEBUG
```

**Configuration Notes:**

- Replace `your_mysql_password` with your MySQL root password
- The `createDatabaseIfNotExist=true` parameter automatically creates the database if it doesn't exist
- `flyway.enabled=true` automatically runs database migrations on startup
- Adjust `server.port` if port 8080 is already in use

### Step 3: Verify MySQL Connection

Test your MySQL connection:

```bash
mysql -h localhost -u root -p
# Enter your password when prompted
# Type 'EXIT;' to quit
```

### Step 4: Build the Project

Use the Gradle wrapper to build the project (no need to install Gradle separately):

```bash
# On Windows
gradlew.bat clean build

# On macOS/Linux
./gradlew clean build
```

This command:

- Cleans previous build artifacts
- Downloads all dependencies
- Compiles the source code
- Runs unit tests
- Creates the application JAR file

### Step 5: Run the Application

Start the Spring Boot application using the Gradle wrapper:

```bash
# On Windows
gradlew.bat bootRun

# On macOS/Linux
./gradlew bootRun
```

**Expected Output:**

```
...
2026-05-20 14:32:15.123  INFO 12345 --- [main] c.p.rcrsms.RcrsmsApplication :
Started RcrsmsApplication in 5.234 seconds (JVM running for 5.987)
```

### Step 6: Access the Application

Open your web browser and navigate to:

```
http://localhost:8080
```

You will be presented with the login page. Use the default credentials:

- **Admin Account**: `admin` / `admin123`
- **Sample Participant**: `participant1` / `password123`

### Step 7: Database Migrations

Database schema is automatically created and maintained by **Flyway** on application startup. Migration files are located in:

```
src/main/resources/db/migrations/
```

Migrations are applied in order:

- `V1__create_user_schema.sql` - User table
- `V2__create_conference_schema.sql` - Conference table
- `V3__create_session_schema.sql` - Session table
- `V4__create_registration_schema.sql` - Registration table (junction)
- `V5__create_room_schema.sql` - Room table
- `V6__add_contraints_to_some_tables.sql` - Constraints & indexes
- `V7__add_attendance_column.sql` - Attendance tracking

---

## Project Structure

```
SE-Registration-and-Session-Management-System/
├── src/
│   ├── main/
│   │   ├── java/com/project5/rcrsms/
│   │   │   ├── Entity/              # JPA Entity classes
│   │   │   ├── Repository/          # Spring Data JPA repositories
│   │   │   ├── Service/             # Business logic layer
│   │   │   ├── controller/          # MVC controllers
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── Security/            # Security configuration
│   │   │   └── RcrsmsApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migrations/       # Flyway migration scripts
│   │       └── templates/           # Thymeleaf HTML templates
│   └── test/                        # Unit tests
├── build.gradle                     # Gradle configuration
├── gradlew / gradlew.bat           # Gradle wrapper
└── README.md                        # This file
```

---

## Key Features

✅ **User Authentication & Authorization**

- Role-based access control (Admin, Chair, Participant)
- JWT token-based authentication
- Secure password hashing

✅ **Conference Management**

- Create and manage multiple conferences
- Set conference dates and locations
- Track session status (Pending, Approved, Rejected)

✅ **Session Organization**

- Create sessions within conferences
- Assign session chairs and rooms
- Validate session times (must be in future)
- Support proposal abstracts

✅ **Registration System**

- Prevent duplicate registrations
- Track participant registrations
- Monitor attendance

✅ **Database Integrity**

- Enforced constraints at both ORM and database layers
- Automatic cascading operations
- Data consistency guarantees

---

## Troubleshooting

### Issue: Connection refused - MySQL not running

**Solution**: Ensure MySQL server is running on your system (typically on port 3306)

### Issue: "Unknown database 'rcrsms_db'"

**Solution**: MySQL will automatically create the database with `createDatabaseIfNotExist=true` in the connection URL

### Issue: Gradle build fails

**Solution**: Clear cache and rebuild

```bash
gradlew clean --refresh-dependencies build
```

### Issue: Port 8080 already in use

**Solution**: Change the port in `application.properties`:

```properties
server.port=8081
```

---

## Contributing

Contributions are welcome! Please ensure:

1. Code follows Java naming conventions and Spring Boot patterns
2. New features include appropriate tests
3. Database changes include Flyway migration scripts
4. Commit messages are clear and descriptive

---

## License

This project is part of academic coursework for Software Engineering Year 4, Semester 1.

---

## Contact & Support

For questions or issues, please open an issue on the project repository.

---

**Built with ❤️ using Spring Boot, Java, and MySQL**
