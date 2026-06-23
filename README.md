# Java Maven Demo - Todo REST API

A Spring Boot 3.2 (Java 17) backend exposing a CRUD REST API for Todo items,
deployed on NevTan Cloud. Built with a standard layered architecture
(Controller -> Service -> Repository -> Entity), MySQL via Spring Data JPA,
Bean Validation, centralized exception handling, and OpenAPI/Swagger docs.

## Architecture

```
com.example.demo
 |- controller   REST endpoints (TodoController, Greeter)
 |- service      Business logic (TodoService, TodoServiceImpl)
 |- repository   Spring Data JPA (TodoRepository)
 |- entity       JPA entities (Todo)
 |- dto          Request/response models (TodoRequest, TodoResponse)
 |- exception    Error handling (GlobalExceptionHandler, ErrorResponse, ...)
 |- config       OpenAPI and CORS configuration
```

## API Endpoints

Base path: `/api/todos`

| Method | Path             | Description        |
|--------|------------------|--------------------|
| GET    | /api/todos       | List all todos     |
| GET    | /api/todos/{id}  | Get a todo by id   |
| POST   | /api/todos       | Create a todo      |
| PUT    | /api/todos/{id}  | Update a todo      |
| DELETE | /api/todos/{id}  | Delete a todo      |

Request body for POST/PUT:

```json
{ "title": "Buy milk", "completed": false }
```

`title` is required (non-blank, max 255 chars).

## API Documentation (Swagger)

- Swagger UI: `/swagger-ui.html`
- OpenAPI spec: `/v3/api-docs`

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8 (for running locally against a real database)

## Configuration

Database credentials are read from environment variables (never hardcoded).
Defaults target a local MySQL instance:

| Variable              | Default                                              |
|-----------------------|------------------------------------------------------|
| DB_URL                | jdbc:mysql://localhost:3306/tododb?...               |
| DB_USERNAME           | root                                                 |
| DB_PASSWORD           | (empty)                                              |
| CORS_ALLOWED_ORIGINS  | http://localhost:5173,http://localhost:3000          |

## Run Locally

1. Start MySQL and create (or let the app create) the `tododb` database.
2. Export your credentials:

```bash
export DB_URL="jdbc:mysql://localhost:3306/tododb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your-password"
```

3. Build and run:

```bash
mvn clean package
mvn spring-boot:run
```

The API starts on http://localhost:8080.

## Run Tests

Tests use an in-memory H2 database (no MySQL required):

```bash
mvn test
```

## Connect the React Frontend

CORS is preconfigured for `http://localhost:5173` (Vite) and
`http://localhost:3000`. In the React app, call the API, e.g.:

```js
const API = "http://localhost:8080/api/todos";
await fetch(API).then((r) => r.json());        // list
await fetch(API, {                              // create
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ title: "New task", completed: false }),
});
```

For other origins, set `CORS_ALLOWED_ORIGINS` accordingly.

## Deployment Note (NevTan)

The deployed container needs a reachable MySQL instance. Set `DB_URL`,
`DB_USERNAME`, and `DB_PASSWORD` as environment variables in the NevTan
project settings before redeploying, otherwise the app will fail to start.
