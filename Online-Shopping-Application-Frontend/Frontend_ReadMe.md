# Online Shopping Application - Frontend

Angular 14 frontend application for SuperDuperMart online shopping platform.

## Features

- User authentication (Login/Register)
- Product browsing and management
- Shopping cart (stored in localStorage)
- Order management
- Watchlist functionality
- Admin dashboard with statistics
- Product management (CRUD operations)
- Order management for admins

## Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:4200`

## Backend API

Make sure the backend server is running on `http://localhost:8080`

## Project Structure

- `src/app/components/` - All Angular components
- `src/app/services/` - Services for API calls and business logic
- `src/app/models/` - TypeScript interfaces/models
- `src/app/guards/` - Route guards for authentication

## Key Features

### User Features
- Login/Register
- Browse products
- Add to cart (localStorage)
- Add to watchlist
- View orders
- Cancel orders
- View statistics (frequent/recent purchases)

### Admin Features
- Product management (Create, Read, Update)
- Order management (View, Cancel, Complete)
- Statistics dashboard
- User management

## Technologies

- Angular 14
- Angular Material
- TypeScript
- RxJS
- LocalStorage API

