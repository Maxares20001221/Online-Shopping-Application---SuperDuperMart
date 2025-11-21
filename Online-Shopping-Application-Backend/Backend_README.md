# SuperDuperMart - Online Shopping Platform

A comprehensive RESTful e-commerce platform built with Spring Boot, featuring user authentication, product management, shopping cart, order processing, and administrative functionalities. The application implements role-based access control (RBAC) with JWT authentication and provides statistical insights for both users and administrators.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Key Features](#key-features)
- [Implementation Details](#implementation-details)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Getting Started](#getting-started)

---

## Overview

SuperDuperMart is a full-stack e-commerce application that enables users to browse products, manage shopping carts, place orders, and maintain watchlists. Administrators can manage products, view all orders, update order statuses, and access business analytics. The system implements secure authentication using JWT tokens and enforces role-based permissions to ensure data security and proper access control.

### Key Capabilities

- **User Management**: Registration, login, and role-based access (USER/ADMIN)
- **Product Management**: CRUD operations with role-based visibility (users cannot see stock quantities)
- **Shopping Cart**: Add, update, remove items, and clear cart
- **Order Processing**: Place orders, view order history, cancel/complete orders with automatic stock management
- **Watchlist**: Save favorite products for later viewing
- **Statistics**: User purchase history and admin business analytics
- **Security**: JWT-based authentication with Spring Security

---

## Technology Stack

### Backend Framework
- **Spring Boot 2.7.14**: Core application framework
- **Java 8**: Programming language

### Data Access
- **Hibernate 5.x**: ORM framework (manually configured, not using Spring Data JPA)
- **MySQL 8.0**: Relational database
- **Custom DAO Layer**: Abstract base DAO pattern for data access

### Security
- **Spring Security**: Authentication and authorization
- **JWT (JSON Web Tokens)**: Stateless authentication using `io.jsonwebtoken:jjwt:0.9.1`
- **BCryptPasswordEncoder**: Password encryption

### Additional Libraries
- **Spring AOP**: Aspect-oriented programming for logging
- **Spring Boot Validation**: Input validation (dependency included but not actively used)
- **Jackson**: JSON serialization/deserialization

### Build Tool
- **Maven**: Dependency management and build automation

---

## Architecture

### Layered Architecture

The application follows a **three-tier architecture** pattern:

```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)    │
│  - Handles HTTP requests/responses  │
│  - Input validation & DTO mapping  │
│  - Role-based access control        │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer (Business Logic) │
│  - Business rules & validations     │
│  - Transaction management          │
│  - Orchestrates DAO operations    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│        DAO Layer (Data Access)      │
│  - Database operations              │
│  - Hibernate session management    │
│  - Query execution                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Entity Layer (Domain Model)    │
│  - JPA entities                     │
│  - Database table mappings         │
└─────────────────────────────────────┘
```

### Design Patterns

1. **Template Method Pattern**: `AbstractHibernateDao<T>` provides common CRUD operations
2. **DTO Pattern**: Data Transfer Objects separate API contracts from database entities
3. **Repository Pattern**: DAO classes encapsulate data access logic
4. **Strategy Pattern**: Role-based access control with different behaviors for USER/ADMIN
5. **Aspect-Oriented Programming**: Cross-cutting concerns (logging) handled via AOP

### Security Architecture

```
Client Request
    ↓
JwtRequestFilter (Validates JWT token)
    ↓
SecurityConfig (URL-based access control)
    ↓
Controller (Role-based method execution)
    ↓
Service Layer (Business logic with @Transactional)
```

**Authentication Flow**:
1. User registers/logs in via `/signup` or `/login`
2. Server generates JWT token containing email and role
3. Client includes token in `Authorization: Bearer <token>` header
4. `JwtRequestFilter` validates token on each request
5. `SecurityContext` populated with user authentication details
6. Controllers check user role for authorization

---

## Project Structure

```
src/main/java/com/example/superdupermart/
├── aop/                          # Aspect-Oriented Programming
│   └── LoggingAspect.java       # Logs method executions
│
├── config/                       # Configuration Classes
│   ├── HibernateConfig.java      # Hibernate session factory & transaction manager
│   └── HibernateProperty.java   # Database connection properties
│
├── controller/                   # REST Controllers
│   ├── AuthController.java      # User registration & login
│   ├── ProductController.java   # Product CRUD operations
│   ├── OrderController.java     # Order management
│   ├── CartController.java      # Shopping cart operations
│   ├── WatchlistController.java # Watchlist management
│   ├── AdminController.java     # Admin-only operations
│   └── StatsController.java     # Statistics endpoints
│
├── dao/                          # Data Access Objects
│   ├── AbstractHibernateDao.java # Base DAO with common CRUD
│   ├── UserDao.java
│   ├── ProductDao.java
│   ├── OrderDao.java
│   ├── OrderItemDao.java
│   ├── CartDao.java
│   ├── CartItemDao.java
│   ├── WatchlistDao.java
│   └── PermissionDao.java      # (Defined but not actively used)
│
├── dto/                          # Data Transfer Objects
│   ├── ProductDTO.java          # Product response (hides wholesalePrice)
│   ├── OrderResponseDTO.java    # Order details with items
│   ├── CartResponseDTO.java    # Cart summary with items
│   ├── CartItemDTO.java        # Individual cart item
│   ├── OrderItemDTO.java       # Individual order item
│   ├── WatchlistDTO.java       # Watchlist item
│   ├── StatsDTO.java           # Statistics data
│   ├── LoginRequestDTO.java    # Login request payload
│   └── UserRegistrationDTO.java # Registration payload
│
├── entity/                       # JPA Entities
│   ├── User.java               # User account (role: USER/ADMIN)
│   ├── Product.java            # Product catalog
│   ├── Order.java              # Order header
│   ├── OrderItem.java          # Order line items
│   ├── Cart.java               # Shopping cart
│   ├── CartItem.java           # Cart line items
│   ├── Watchlist.java          # User watchlist
│   └── Permission.java         # (Defined but not actively used)
│
├── exception/                   # Exception Handling
│   ├── GlobalExceptionHandler.java # Centralized exception handler
│   └── InvalidCredentialsException.java # Custom auth exception
│
├── security/                    # Security Configuration
│   ├── SecurityConfig.java     # Spring Security configuration
│   ├── JwtTokenUtil.java       # JWT generation & validation
│   └── JwtRequestFilter.java   # JWT authentication filter
│
└── service/                     # Business Logic Layer
    ├── AuthService.java        # Authentication & registration
    ├── ProductService.java     # Product business logic
    ├── OrderService.java       # Order processing & status updates
    ├── CartService.java        # Cart management
    ├── WatchlistService.java   # Watchlist operations
    ├── UserService.java        # User management
    └── StatsService.java       # Statistics calculations
```

---

## Key Features

### 1. User Authentication & Authorization
- **Registration**: Users can register with username, email, and password (passwords are BCrypt encrypted)
- **Login**: JWT token-based authentication with role information embedded
- **Role-Based Access**: Two roles - `USER` (default) and `ADMIN`
- **Security**: All endpoints except `/signup` and `/login` require authentication

### 2. Product Management
- **User View**: 
  - Browse products (only in-stock items visible)
  - View product details (description, price)
  - **Cannot see stock quantities** (security feature)
- **Admin View**:
  - View all products including out-of-stock items
  - See stock quantities
  - Create new products
  - Update product information (name, description, prices, quantity)

### 3. Shopping Cart
- Add products to cart with quantity
- Update item quantities
- Remove individual items
- Clear entire cart
- View cart summary (total items, total price)

### 4. Order Management
- **Place Orders**: From cart or direct product selection
- **Order Status**: Three states - `Processing`, `Completed`, `Canceled`
- **Stock Management**: 
  - Stock automatically deducted when order is placed
  - Stock restored when order is canceled
  - Cannot cancel `Completed` orders or complete `Canceled` orders
- **Order History**: Users can view their own orders; admins can view all orders

### 5. Watchlist
- Add products to watchlist
- View all watchlisted products
- Remove products from watchlist

### 6. Statistics
- **User Statistics**:
  - Most frequently purchased products (top N)
  - Most recently purchased products (top N)
- **Admin Statistics**:
  - Most profitable products (based on profit margin)
  - Most popular products (by sales volume)
- All statistics exclude canceled orders

### 7. Exception Handling
- Centralized exception handling via `GlobalExceptionHandler`
- Custom exceptions (`InvalidCredentialsException`)
- Consistent error response format with timestamps

---

## Implementation Details

### Data Access Layer

**AbstractHibernateDao Pattern**:
```java
public abstract class AbstractHibernateDao<T> {
    protected Session getCurrentSession() { ... }
    public T findById(Long id) { ... }
    public List<T> findAll() { ... }
    public void save(T entity) { ... }
    public void update(T entity) { ... }
    public void deleteById(Long id) { ... }
}
```

All DAO classes extend this base class, providing consistent CRUD operations while allowing custom query methods.

### Transaction Management

- **Service Layer**: All service methods annotated with `@Transactional`
- **Controller Layer**: Statistics endpoints use `@Transactional` for direct DAO access
- **Transaction Manager**: Configured in `HibernateConfig` using `HibernateTransactionManager`

### DTO Pattern Implementation

**Security Benefits**:
- `ProductDTO` hides `wholesalePrice` from regular users
- `ProductDTO.stock` is `null` for users, visible for admins
- Prevents exposure of database relationships (e.g., `Set<OrderItem>`)

**Example Conversion**:
```java
private ProductDTO toDTO(Product p, boolean isAdmin) {
    ProductDTO dto = new ProductDTO();
    dto.setProductId(p.getProductId());
    dto.setName(p.getName());
    dto.setDescription(p.getDescription());
    dto.setPrice(p.getRetailPrice().doubleValue());
    dto.setStock(isAdmin ? p.getQuantity() : null); // Role-based visibility
    return dto;
}
```

### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234571490
}
```

Token is signed with HS256 algorithm and includes user email (subject) and role.

### Order Status Flow

```
Processing → Completed (irreversible)
Processing → Canceled (irreversible, stock restored)
Canceled → Cannot be completed
Completed → Cannot be canceled
```

### Stock Management Logic

- **Order Placement**: `quantity` deducted from `Product.quantity`
- **Order Cancellation**: `quantity` restored to `Product.quantity`
- **Order Completion**: No stock change (already deducted)

---

## API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication
Most endpoints require JWT authentication. Include the token in the request header:
```
Authorization: Bearer <your_jwt_token>
```

### API Endpoints

#### Authentication Endpoints

| Method | Endpoint | Description | Request Body | Response | Auth Required |
|--------|----------|-------------|--------------|-----------|---------------|
| POST | `/signup` | Register a new user account | `{"username": "string", "email": "string", "password": "string"}` | `{"message": "Registration successful"}` | No |
| POST | `/login` | Authenticate user and get JWT token | `{"username": "string", "password": "string"}` | `{"message": "Login successful", "token": "jwt_token", "role": "USER\|ADMIN"}` | No |

**Note**: The `username` field in login request is actually used as `email` in the backend.

---

#### User Product Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/products/all` | Get all in-stock products (users see only available items) | None | `List<ProductDTO>` | Yes (USER) |
| GET | `/products/{productId}` | Get product details by ID (users cannot see stock quantity) | None | `ProductDTO` | Yes (USER) |

**ProductDTO Structure**:
```json
{
  "productId": 1,
  "name": "Product Name",
  "description": "Product description",
  "price": 99.99,
  "stock": null  // null for regular users
}
```

---

#### User Order Endpoints

| Method | Endpoint | Description | Query Params | Request Body | Response | Auth Required |
|--------|----------|-------------|--------------|--------------|----------|---------------|
| POST | `/orders` | Place a new order | `userId` (Long) | `{"order": [{"productId": 1, "quantity": 2}]}` or empty body (from cart) | `{"message": "Order placed"}` | Yes (USER) |
| GET | `/orders/all` | Get all orders for the user | `userId` (Long) | None | `List<OrderResponseDTO>` | Yes (USER) |
| GET | `/orders/{orderId}` | Get order details by ID | None | None | `OrderResponseDTO` | Yes (USER) |
| PATCH | `/orders/{orderId}/cancel` | Cancel an order (only Processing orders) | None | None | `{"message": "Order canceled"}` | Yes (USER) |
| PATCH | `/orders/{orderId}/complete` | Complete an order (only Processing orders) | None | None | `{"message": "Order completed"}` | Yes (USER) |

**OrderResponseDTO Structure**:
```json
{
  "orderId": 1,
  "datePlaced": "2024-01-15T10:30:00",
  "orderStatus": "Processing",
  "totalPrice": 199.98,
  "items": [
    {
      "itemId": 1,
      "productId": 1,
      "productName": "Product Name",
      "quantity": 2,
      "price": 99.99
    }
  ]
}
```

---

#### User Shopping Cart Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/cart/view` | View shopping cart | `userId` (Long) | `CartResponseDTO` | Yes (USER) |
| POST | `/cart/add` | Add product to cart | `userId`, `productId`, `quantity` | `CartResponseDTO` | Yes (USER) |
| PATCH | `/cart/updateQuantity` | Update cart item quantity | `cartItemId`, `newQuantity`, `userId` | `CartResponseDTO` | Yes (USER) |
| DELETE | `/cart/remove/{productId}` | Remove product from cart | `userId` | `CartResponseDTO` | Yes (USER) |
| DELETE | `/cart/clear/{userId}` | Clear entire cart | None | `CartResponseDTO` | Yes (USER) |

**CartResponseDTO Structure**:
```json
{
  "userId": 1,
  "totalItems": 3,
  "totalPrice": 299.97,
  "items": [
    {
      "itemId": 1,
      "productId": 1,
      "productName": "Product Name",
      "quantity": 2,
      "price": 99.99
    }
  ]
}
```

---

#### User Watchlist Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/watchlist/products/all` | Get all watchlisted products | `userId` (Long) | `List<WatchlistDTO>` | Yes (USER) |
| POST | `/watchlist/product/{productId}` | Add product to watchlist | `userId` (Long) | `{"message": "Product added to watchlist"}` | Yes (USER) |
| DELETE | `/watchlist/product/{productId}` | Remove product from watchlist | `userId` (Long) | `{"message": "Product removed from watchlist"}` | Yes (USER) |

**WatchlistDTO Structure**:
```json
{
  "productId": 1,
  "productName": "Product Name",
  "price": 99.99
}
```

---

#### User Statistics Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/products/frequent/{topN}` | Get user's most frequently purchased products | `userId` (Long) | `List<StatsDTO>` | Yes (USER) |
| GET | `/products/recent/{topN}` | Get user's most recently purchased products | `userId` (Long) | `List<StatsDTO>` | Yes (USER) |

**StatsDTO Structure**:
```json
{
  "productName": "Product Name",
  "totalSold": 10,
  "totalRevenue": 999.90
}
```

**Note**: Statistics exclude canceled orders.

---

#### Admin Product Endpoints

| Method | Endpoint | Description | Query Params | Request Body | Response | Auth Required |
|--------|----------|-------------|--------------|--------------|----------|---------------|
| GET | `/products/all` | Get all products (including out-of-stock) | None | None | `List<ProductDTO>` (with stock visible) | Yes (ADMIN) |
| GET | `/products/{productId}` | Get product details (with stock visible) | None | None | `ProductDTO` (with stock visible) | Yes (ADMIN) |
| POST | `/products` | Create a new product | None | `{"name": "string", "description": "string", "wholesalePrice": number, "retailPrice": number, "quantity": number}` | `ProductDTO` | Yes (ADMIN) |
| PATCH | `/products/{productId}` | Update product information | None | `{"name": "string", "description": "string", "wholesalePrice": number, "retailPrice": number, "quantity": number}` | `ProductDTO` | Yes (ADMIN) |

**Admin ProductDTO Structure** (stock is visible):
```json
{
  "productId": 1,
  "name": "Product Name",
  "description": "Product description",
  "price": 99.99,
  "stock": 100  // Visible for admins
}
```

---

#### Admin Order Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/orders/all` | Get all orders (all users) | None | `List<OrderResponseDTO>` | Yes (ADMIN) |
| GET | `/orders/{orderId}` | Get order details | None | `OrderResponseDTO` | Yes (ADMIN) |
| PATCH | `/orders/{orderId}/cancel` | Cancel an order (restores stock) | None | `{"message": "Order canceled"}` | Yes (ADMIN) |
| PATCH | `/orders/{orderId}/complete` | Complete an order | None | `{"message": "Order completed"}` | Yes (ADMIN) |

---

#### Admin User Management Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/admin/users/all` | Get all users | None | `List<User>` | Yes (ADMIN) |
| DELETE | `/admin/users/{userId}` | Delete a user | None | `{"message": "User deleted"}` | Yes (ADMIN) |

---

#### Admin Statistics Endpoints

| Method | Endpoint | Description | Query Params | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| GET | `/products/profit/{topN}` | Get most profitable products (by profit margin) | None | `List<StatsDTO>` | Yes (ADMIN) |
| GET | `/products/popular/{topN}` | Get most popular products (by sales volume) | None | `List<StatsDTO>` | Yes (ADMIN) |

**StatsDTO Structure**:
```json
{
  "productName": "Product Name",
  "totalSold": 50,
  "totalRevenue": 4999.50
}
```

**Note**: Statistics are calculated from `Completed` orders only.

---

## Database Schema

### Entity Relationships

```
User (1) ──── (N) Order
User (1) ──── (1) Cart
User (1) ──── (N) Watchlist

Order (1) ──── (N) OrderItem
Cart (1) ──── (N) CartItem

Product (1) ──── (N) OrderItem
Product (1) ──── (N) CartItem
Product (1) ──── (N) Watchlist
```

### Key Tables

- **users**: User accounts with role (USER/ADMIN)
- **products**: Product catalog with wholesale/retail prices and quantity
- **orders**: Order headers with status (Processing/Completed/Canceled)
- **order_items**: Order line items with purchased price
- **carts**: Shopping cart headers
- **cart_items**: Shopping cart line items
- **watchlists**: User-product watchlist mappings

---

## Security

### Authentication
- **JWT Tokens**: Stateless authentication with HS256 algorithm
- **Token Expiration**: Configurable token expiration time
- **Password Encryption**: BCrypt hashing for passwords

### Authorization
- **Role-Based Access Control (RBAC)**: Two roles - USER and ADMIN
- **URL-Based Security**: Spring Security configuration restricts `/admin/**` to ADMIN role
- **Method-Level Security**: Controllers check user roles for sensitive operations

### Security Features
- Users cannot see product stock quantities
- Users cannot see wholesale prices
- Admin-only endpoints protected by role checks
- JWT token validation on every authenticated request

---

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- MySQL 8.0
- IDE (IntelliJ IDEA recommended)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SuperDuperMart
   ```

2. **Configure Database**
   - Create MySQL database: `superdupermart`
   - Update `src/main/resources/application.yaml` with your database credentials:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/superdupermart?useSSL=false&serverTimezone=UTC
         username: your_username
         password: your_password
     ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or run `SuperDuperMartApplication.java` from your IDE.

5. **Access the API**
   - Base URL: `http://localhost:8080`
   - The application will automatically create database tables on first run (Hibernate `ddl-auto: update`)

### Testing with Postman

1. Import the Postman collection: `src/main/resources/static/OnlineShoppingApp APIs.postman_collection.json`
2. Start with registration: `POST /signup`
3. Login to get JWT token: `POST /login`
4. Copy the token and set it in Postman's Authorization header (Bearer Token)
5. Test the endpoints according to your role (USER or ADMIN)

### Creating an Admin User

By default, new users are registered as `USER` role. To create an admin:
1. Register a user via `/signup`
2. Manually update the database:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'your_email@example.com';
   ```

---

## Error Handling

The application uses a centralized exception handler (`GlobalExceptionHandler`) that returns consistent error responses:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "error": "Error message",
  "status": 400
}
```

Common HTTP status codes:
- `200 OK`: Successful request
- `400 Bad Request`: Invalid input or business logic error
- `401 Unauthorized`: Invalid or missing JWT token
- `403 Forbidden`: Insufficient permissions
- `500 Internal Server Error`: Server-side error

---

## Logging

The application uses Spring AOP for method-level logging via `LoggingAspect`. All controller methods are logged with execution time and parameters.

---

## Future Enhancements

Potential improvements:
- Input validation using Spring Validation annotations
- Pagination for order/product lists
- Product image upload and storage
- Email notifications for order status changes
- Payment integration
- Product search and filtering
- User profile management

---

## License

This project is developed for educational purposes.

---

## Contact

For questions or issues, please refer to the project repository.
