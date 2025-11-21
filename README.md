# SuperDuperMart - Online Shopping Application

A full-stack e-commerce platform built with **Angular 14** and **Spring Boot**, featuring user authentication, product management, shopping cart, order processing, and administrative functionalities. The application implements role-based access control (RBAC) with JWT authentication and provides real-time updates and statistical insights for both users and administrators.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-brightgreen.svg)
![Angular](https://img.shields.io/badge/Angular-14-red.svg)
![Java](https://img.shields.io/badge/Java-8-orange.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-4.7-blue.svg)

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Architecture](#-architecture)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### User Features
- 🔐 **User Authentication**: Secure registration and login with JWT tokens
- 🛍️ **Product Browsing**: Browse products with detailed descriptions and pricing
- 🛒 **Shopping Cart**: Add, update, remove items with real-time synchronization
- 📋 **Order Management**: Place orders, view order history, and track order status
- ⭐ **Watchlist**: Save favorite products for later viewing
- 📊 **Statistics**: View frequent and recent purchase history
- 👤 **User Profile**: Personalized homepage with order history and statistics

### Admin Features
- 📦 **Product Management**: Full CRUD operations for products (Create, Read, Update, Delete)
- 📊 **Order Management**: View all orders, update order status (Pending, Processing, Completed, Cancelled)
- 👥 **User Management**: View and manage user accounts
- 📈 **Business Analytics**: View most profitable and popular products
- 🔒 **Role-Based Access Control**: Secure admin-only endpoints

### Technical Features
- 🔄 **Real-time Updates**: Cart and watchlist updates reflect immediately across components
- 🔐 **JWT Authentication**: Stateless authentication with secure token management
- 🛡️ **User Data Isolation**: Each user can only access their own data (cart, orders, watchlist)
- 📱 **Responsive Design**: Modern UI built with Angular Material
- 🚀 **RESTful API**: Clean and well-structured REST endpoints

---

## 🛠️ Technology Stack

### Frontend
- **Framework**: Angular 14.3
- **UI Library**: Angular Material 14.2
- **Language**: TypeScript 4.7
- **State Management**: RxJS Observables
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router with Guards

### Backend
- **Framework**: Spring Boot 2.7.14
- **Language**: Java 8
- **ORM**: Hibernate 5.x (manually configured)
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **AOP**: Spring AOP for logging

### Security
- **Authentication**: JWT (JSON Web Tokens)
- **Password Encryption**: BCrypt
- **CORS**: Configured for cross-origin requests
- **Role-Based Access**: USER and ADMIN roles

---

## 📁 Project Structure

```
Online-Shopping-Application/
├── Online-Shopping-Application-Frontend/     # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/                   # UI components
│   │   │   │   ├── login/                   # Login/Register
│   │   │   │   ├── user/                    # User components
│   │   │   │   └── admin/                   # Admin components
│   │   │   ├── services/                    # Business logic services
│   │   │   │   ├── api.service.ts           # HTTP API calls
│   │   │   │   ├── auth.service.ts          # Authentication
│   │   │   │   └── cart.service.ts          # Cart management
│   │   │   ├── models/                      # TypeScript interfaces
│   │   │   ├── guards/                     # Route guards
│   │   │   └── app.module.ts               # Main module
│   │   └── index.html                      # Entry point
│   ├── package.json
│   └── angular.json
│
└── Online-Shopping-Application-Backend/      # Spring Boot backend
    ├── src/main/java/com/example/superdupermart/
    │   ├── controller/                      # REST controllers
    │   ├── service/                         # Business logic
    │   ├── dao/                            # Data access objects
    │   ├── entity/                         # JPA entities
    │   ├── dto/                           # Data transfer objects
    │   ├── security/                      # Security configuration
    │   └── config/                        # Configuration classes
    ├── src/main/resources/
    │   ├── application.yaml               # Application configuration
    │   └── static/                        # Static resources
    └── pom.xml                            # Maven dependencies
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 8** or higher
- **Node.js** 14.x or higher
- **npm** 6.x or higher
- **MySQL** 8.0
- **Maven** 3.6+ (optional, Maven Wrapper included)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Online-Shopping-Application/Online-Shopping-Application-Backend
   ```

2. **Configure Database**
   - Create a MySQL database named `superdupermart`
   - Copy the example configuration file:
     ```bash
     cp src/main/resources/application.yaml.example src/main/resources/application.yaml
     ```
   - Update database credentials in `src/main/resources/application.yaml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/superdupermart?useSSL=false&serverTimezone=UTC
         username: your_username
         password: your_password
     ```

3. **Build and Run**
   ```bash
   # Using Maven Wrapper
   ./mvnw spring-boot:run
   
   # Or using Maven
   mvn spring-boot:run
   ```

4. **Backend will be available at**: `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd Online-Shopping-Application-Frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

4. **Frontend will be available at**: `http://localhost:4200`

### Default Credentials

After setting up the database, you can register a new user through the registration page, or create an admin user directly in the database.

---

## 🏗️ Architecture

### Backend Architecture

The backend follows a **layered architecture** pattern:

```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │
│  - Handles HTTP requests/responses  │
│  - Input validation & DTO mapping   │
│  - Role-based access control        │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer (Business Logic) │
│  - Business rules & validations     │
│  - Transaction management          │
│  - Orchestrates DAO operations     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│        DAO Layer (Data Access)      │
│  - Database operations              │
│  - Hibernate session management     │
│  - Query execution                  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Entity Layer (Domain Model)    │
│  - JPA entities                     │
│  - Database table mappings          │
└─────────────────────────────────────┘
```

### Frontend Architecture

The frontend follows a **component-based architecture**:

```
┌─────────────────────────────────────┐
│         Components (UI)              │
│  - Display data and handle events    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Services (Business Logic)   │
│  - API calls                        │
│  - State management                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         HttpClient (HTTP Client)    │
│  - Send requests to backend         │
│  - Handle responses                 │
└─────────────────────────────────────┘
```

### Design Patterns

- **Template Method Pattern**: `AbstractHibernateDao<T>` provides common CRUD operations
- **DTO Pattern**: Separates API contracts from database entities
- **Dependency Injection**: Spring IoC container manages object dependencies
- **Repository Pattern**: DAO layer abstracts database access
- **Observer Pattern**: RxJS Observables for reactive programming

---

## 📚 API Documentation

### Authentication Endpoints

- `POST /signup` - User registration
- `POST /login` - User login (returns JWT token)

### Product Endpoints

- `GET /products/all` - Get all products (filtered by role)
- `GET /products/{productId}` - Get product details
- `POST /products` - Create product (Admin only)
- `PATCH /products/{productId}` - Update product (Admin only)

### Cart Endpoints

- `GET /cart/view?userId={userId}` - Get user's cart
- `POST /cart/add?userId={userId}&productId={productId}&quantity={quantity}` - Add item to cart
- `PATCH /cart/updateQuantity?cartItemId={id}&newQuantity={qty}&userId={userId}` - Update quantity
- `DELETE /cart/remove/{productId}?userId={userId}` - Remove item
- `DELETE /cart/clear/{userId}` - Clear cart

### Order Endpoints

- `GET /orders/all?userId={userId}` - Get orders (filtered by user)
- `POST /orders?userId={userId}` - Place order
- `GET /orders/{orderId}` - Get order details
- `PATCH /orders/{orderId}/cancel` - Cancel order
- `PATCH /orders/{orderId}/complete` - Complete order

### Watchlist Endpoints

- `GET /watchlist/products/all?userId={userId}` - Get watchlist
- `POST /watchlist/product/{productId}?userId={userId}` - Add to watchlist
- `DELETE /watchlist/product/{productId}?userId={userId}` - Remove from watchlist

### Admin Endpoints

- `GET /admin/users` - Get all users
- `DELETE /admin/deleteUser/{userId}` - Delete user
- `GET /admin/orders` - Get all orders
- `PUT /admin/updateOrderStatus/{orderId}?status={status}` - Update order status

### Statistics Endpoints

- `GET /products/frequent/{topN}?userId={userId}` - Get frequent products
- `GET /products/recent/{topN}?userId={userId}` - Get recent products
- `GET /products/profit/{topN}` - Get most profitable products (Admin)
- `GET /products/popular/{topN}` - Get most popular products (Admin)

**Note**: All endpoints (except `/signup` and `/login`) require JWT authentication. Include the token in the `Authorization` header: `Bearer {token}`

For detailed API documentation, see:
- Backend: `Online-Shopping-Application-Backend/Backend_README.md`
- Frontend API Guide: `Online-Shopping-Application-Frontend/前端开发API文档.md`

---

## 🔒 Security

### Authentication Flow

1. User registers/logs in through `/signup` or `/login`
2. Backend validates credentials and generates JWT token
3. Frontend stores token in `localStorage`
4. All subsequent requests include token in `Authorization` header
5. `JwtRequestFilter` validates token on each request
6. User information is stored in `SecurityContext` for authorization

### Authorization

- **Public Endpoints**: `/signup`, `/login`
- **User Endpoints**: All `/cart/*`, `/orders/*`, `/watchlist/*` endpoints (users can only access their own data)
- **Admin Endpoints**: All `/admin/*` endpoints

### User Data Isolation

The application ensures that:
- Users can only view and modify their own cart, orders, and watchlist
- Admin users can access all data
- Controller-level validation prevents unauthorized access

### Password Security

- Passwords are encrypted using **BCrypt** before storage
- Plain text passwords are never stored in the database
- Password validation: minimum 6 characters (configurable)

---

## 📸 Screenshots

### User Interface
- **Login/Register Page**: Clean authentication interface
- **Product List**: Browse all available products
- **Product Details**: View detailed product information
- **Shopping Cart**: Manage cart items with real-time updates
- **Order History**: Track past and current orders
- **Watchlist**: Save favorite products

### Admin Interface
- **Admin Dashboard**: Overview of system statistics
- **Product Management**: CRUD operations for products
- **Order Management**: View and manage all orders
- **User Management**: View and manage users

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow the existing code style
- Write clear commit messages
- Add comments for complex logic
- Test your changes before submitting

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](Online-Shopping-Application-Backend/LICENSE) file for details.

---

## 👨‍💻 Author

**Your Name**

- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Angular team for the powerful frontend framework
- All contributors and open-source libraries used in this project

---

## 📖 Additional Documentation

For more detailed information, please refer to:

- **Backend Architecture**: `Online-Shopping-Application-Backend/代码架构说明文档.md`
- **Backend Principles** (Chinese): `Online-Shopping-Application-Backend/后端原理详解.md`
- **Frontend Principles** (Chinese): `Online-Shopping-Application-Frontend/前端原理详解.md`
- **Backend README**: `Online-Shopping-Application-Backend/Backend_README.md`
- **Frontend README**: `Online-Shopping-Application-Frontend/README.md`

---

## 🐛 Known Issues

- None at the moment. Please report any issues you encounter.

---

## 🔮 Future Enhancements

- [ ] Payment integration
- [ ] Email notifications
- [ ] Product reviews and ratings
- [ ] Search and filter functionality
- [ ] Image upload for products
- [ ] Multi-language support
- [ ] Mobile app version

---

**⭐ If you find this project helpful, please consider giving it a star!**

