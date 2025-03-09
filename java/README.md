# ServEase - Local Service Booking System

## Database

Inside the `<project-root>/database/` directory, you'll find an executable Bash script (`.sh` file) and several SQL scripts (`.sql` files). These can be used to build and rebuild a PostgreSQL database for the ServEase project.

From a terminal session, execute the following commands:

```
cd <project-root>/database/
./create.sh
```

This Bash script drops the existing database if necessary, creates a new database named `servease_db`, and runs the various SQL scripts in the correct order. You don't need to modify the Bash script unless you want to change the database name.

Each SQL script has a specific purpose as described here:

| File Name   | Description |
|------------|-------------|
| `data.sql` | Populates the database with setup or test/demo data. Modify as needed. |
| `dropdb.sql` | Destroys the database so it can be recreated. Drops database and users. No modification needed. |
| `schema.sql` | Creates all database objects, such as tables and sequences. Modify as needed. |
| `user.sql` | Creates database application users and grants privileges. No modification needed. |

### Database Users

The database superuser—`postgres`—must only be used for administration. Instead, ServEase creates two database users:

| Username         | Description |
|-----------------|-------------|
| `servease_owner` | Schema owner with full privileges for database administration. |
| `servease_appuser` | Used by the application for connections. Granted `SELECT`, `INSERT`, `UPDATE`, and `DELETE` privileges. |

## Spring Boot Backend
Spring Boot is configured to run on port `9000` to avoid conflicts with the Vue frontend server.

### Datasource Configuration
Located in `src/resources/application.properties`:
```properties
# datasource connection properties
spring.datasource.url=jdbc:postgresql://localhost:5432/servease_db
spring.datasource.name=servease_db
spring.datasource.username=servease_appuser
spring.datasource.password=serveasepassword
```

### JdbcTemplate Example
Example DAO using JdbcTemplate:
```java
@Service
public class JdbcServiceDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcServiceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
```

### CORS Configuration
Controllers that interact with the frontend must have `@CrossOrigin`:
```java
@RestController
@CrossOrigin
public class ServiceController {
    // Controller logic here
}
```

## Authentication & Security
The `AuthenticationController` manages user login and registration.
It interacts with `JdbcUserDao` to handle authentication.

## Testing
### DAO Integration Tests
Use `com.servease.dao.BaseDaoTest` as the base class for DAO integration tests. Test data is stored in `src/test/resources/test-data.sql`.

---
This document will be updated as the ServEase system evolves!

