# 🏨 Hotel Staff Management System – Spring Boot Microservices with Eureka & Security

## 🧱 Project Structure

This project is built with **Spring Boot Microservices** architecture and includes:

| Service           | Description                                   | Port  |
|------------------|-----------------------------------------------|-------|
| **Eureka Server**| Service Registry to manage all services       | `3001`|
| **Hotel Staff Service** | Provides REST APIs for Hotel & Staff entities | `3002`|
| **Client Web App** | Web interface secured with Spring Security   | `3003`|

---

## 🚀 How to Run

1. **Start Eureka Server**
   - This runs on port `3001`

2. **Start the Hotel Staff Microservice**
   - This registers with Eureka and runs on port `3002`

3. **Start the Client Manager App**
   - This is the Spring Boot web app with login functionality, running on port `3003`

---

## 🗃️ SQL Initialization

SQL file is already provided in the microservice app:

📄 `src/main/java/query.sql`

> ✅ No need to write SQL manually — just run the file to create `hotel`, `staff`, and `user` tables.

Ensure the `user` table has a `VARCHAR(255)` column for `password` (for BCrypt).

---

## 🔐 Setup Default Users

After running the client app (port `3003`), visit this route:
http://localhost:3003/setupusers


This will:

- Insert 2 default users into the MySQL database:
    - 👤 `admin1` with role `Admin`
    - 👤 `staff1` with role `Staff`
- Passwords are encrypted using **BCrypt**
- Plain password for both: `qwe123`

Console will print:  
✅ `User successfully created`

---

## 🔑 Login Details

| Username | Role  | Password |
|----------|-------|----------|
| admin1   | Admin | qwe123   |
| staff1   | Staff | qwe123   |

Access the login page at:  
👉 `http://localhost:3003/login`

---

## 📂 Key Routes

- Eureka dashboard: `http://localhost:3001`
- Hotel API: `http://localhost:3002/api/hotels`
- Staff API: `http://localhost:3002/api/staff`
- Client homepage: `http://localhost:3003`
- Admin dashboard: `http://localhost:3003/admin/dashboard`
- Setup users: `http://localhost:3003/setupusers`

---

## 👩‍💻 Tech Stack

- Spring Boot 3
- Spring Security 6
- Spring Data JPA (MySQL)
- Eureka Discovery Server
- RESTful Microservices
- Thymeleaf (for front-end)

---

## 📌 Notes

- Spring Security redirects users to `/login` for protected routes
- `hasAuthority("Admin")` and `hasAuthority("Staff")` used for role-based access
- Thymeleaf is used to display data, handle login/logout, and secure views

---

