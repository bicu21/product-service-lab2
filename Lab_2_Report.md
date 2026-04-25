# Lab 2 Report: RESTful Product Catalogue API

**Course / Module:** [Insert Course Name]
**Date:** April 25, 2026
**GitHub Repository:** [bicu21/product-service-lab2](https://github.com/bicu21/product-service-lab2)

---

## 1. Objective
The primary objective of this lab was to transition from a basic REST API (Lab 1) to a fully functional and robust **RESTful Product Catalogue API**. This involved implementing complete CRUD (Create, Read, Update, Delete) operations, incorporating input validation, utilizing the RFC 7807 `ProblemDetail` specification for error handling, integrating Swagger/OpenAPI for documentation, and validating the implementation through MockMvc integration tests.

## 2. Technologies Used
* **Language:** Java 17
* **Framework:** Spring Boot 3.3.0
* **Data Access:** Spring Data JPA
* **Database:** H2 (In-Memory Database)
* **API Documentation:** Springdoc OpenAPI (Swagger UI)
* **Testing:** JUnit 5, Spring Boot Test (MockMvc)
* **Build Tool:** Apache Maven

---

## 3. Implementation Details

### 3.1. Project Configuration
The `pom.xml` was updated to include essential dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `h2`, and `springdoc-openapi-starter-webmvc-ui`. The `application.properties` file was configured to establish an in-memory H2 database connection, enable auto-generation of the database schema (`create-drop`), and define the Swagger-UI endpoint path (`/swagger-ui.html`).

### 3.2. Domain Model (`Product`)
A `Product` entity was created and annotated with `@Entity` to map it to the `products` table. The model includes fields for `id`, `name`, `price`, `stockQty`, and `category`. Jakarta Validation annotations (`@NotBlank`, `@DecimalMin`, `@Min`) were strictly applied to ensure data integrity at the entity level.

### 3.3. Data Transfer Objects (DTOs)
To decouple the persistence layer from the presentation layer, two DTOs were implemented:
*   **`ProductRequest`**: Used for capturing input during POST and PUT requests. It contains strict validation constraints (e.g., ensuring `price > 0` and `name` is not blank).
*   **`ProductResponse`**: Used to standardize the JSON output returned to the client.

### 3.4. Repository & Service Layers
*   **`ProductRepository`**: Created as an interface extending `JpaRepository<Product, Long>`. Custom query methods (`findByCategory` and `findByNameContainingIgnoreCase`) were defined for future extensibility.
*   **`ProductService`**: Encapsulates the core business logic. It handles the mapping between Entities and DTOs and acts as the intermediary between the Controller and the Repository. It throws a `ResourceNotFoundException` when an invalid ID is requested.

### 3.5. REST Controller (`ProductController`)
A `@RestController` was mapped to `/api/v1/products`. It exposes the following endpoints:
1.  `GET /api/v1/products` - Returns a list of all products (Status: 200 OK).
2.  `GET /api/v1/products/{id}` - Returns a specific product or a 404 error.
3.  `POST /api/v1/products` - Creates a new product, returning a `201 Created` status with the `Location` header containing the URI of the newly created resource.
4.  `PUT /api/v1/products/{id}` - Updates an existing product entirely (Status: 200 OK).
5.  `DELETE /api/v1/products/{id}` - Deletes a product, returning a `204 No Content`.

### 3.6. Global Error Handling
A central `@RestControllerAdvice` class (`GlobalExceptionHandler`) was implemented extending `ResponseEntityExceptionHandler`. 
*   **404 Not Found**: Handled by catching `ResourceNotFoundException`.
*   **400 Bad Request**: Handled by overriding `handleMethodArgumentNotValid` for validation failures.
Both handlers utilize the Spring Boot 3 **`ProblemDetail`** object to provide standard, structured JSON error responses containing an error `type`, `title`, `status`, and specific `detail`.

### 3.7. API Documentation (OpenAPI / Swagger)
The main application class (`ProductServiceApplication`) was decorated with `@OpenAPIDefinition` to supply API metadata. Swagger UI was successfully activated, rendering an interactive webpage listing all 5 CRUD operations, parameter schemas, and response types.

### 3.8. Integration Testing
A comprehensive integration test suite (`ProductControllerTest`) was created using `@SpringBootTest`, `@AutoConfigureMockMvc`, and `@Transactional`. The tests simulated real HTTP requests to verify the Controller's behavior without launching an actual server. 
*   Tests evaluated successful paths (200 OK, 201 Created with Location headers, 204 No Content).
*   Tests explicitly evaluated negative paths (404 Not Found, 400 Bad Request for invalid price and blank names), validating that the application correctly rejects malformed JSON payloads.

---

## 4. Results & Conclusion
All objectives of Lab 2 were successfully met. The application compiles cleanly using Maven. The execution of `mvn clean compile test` resulted in **9/9 tests passing**, confirming the reliability of the business logic, validation rules, and endpoint configurations. 

Furthermore, the application was successfully deployed locally via `mvn spring-boot:run`. The H2 console and Swagger UI functioned perfectly. Finally, the codebase was securely pushed to the remote GitHub repository for version control and assessment.
