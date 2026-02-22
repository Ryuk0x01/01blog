# 01Blog - Social Blogging Platform for Students

01Blog is a fullstack social blogging platform where students can document their learning journeys, follow peers, and engage in meaningful discussions.

## 🚀 Technologies Used

### Backend
- **Java 21** with **Spring Boot 3**
- **Spring Security** with **JWT** for authentication and RBAC
- **Spring Data JPA** with **PostgreSQL**
- **Lombok** for boilerplate reduction
- **Commons IO** for file handling

### Frontend
- **Angular 18** (Standalone components)
- **Angular Material** for responsive UI components
- **Vanilla CSS** for custom premium styling
- **RxJS** for reactive data handling

## ✨ Key Features

- **Authentication**: Secure registration and login with role-based access (User/Admin).
- **Personal Blocks**: Every user has a public profile listing their posts.
- **Post Management**: CRUD operations for posts with support for images and videos.
- **Social Interactions**: Follow/Unfollow system, Likes, and real-time-feel Comments.
- **Notifications**: Updates when followed users publish new content.
- **Moderation**: Admin panel to manage users (Ban/Unban) and posts (Hide/Show or Delete).
- **Reporting**: User-to-admin reporting system with confirmation prompts.

## 🛠️ Setup Instructions

### Prerequisites
- JDK 21+
- Node.js 20+
- Docker & Docker Compose (for PostgreSQL)

### Step 1: Start the Database
Run the provided docker-compose file to start PostgreSQL:
```bash
docker-compose up -d
```

### Step 2: Run the Backend
Navigate to the `backend` directory and run:
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

### Step 3: Run the Frontend
Navigate to the `frontend` directory and run:
```bash
npm install
npm start
```
The application will be available at `http://localhost:4200`.

## 📂 Project Structure
- `/backend`: Spring Boot source code and configurations.
- `/frontend`: Angular application source code.
- `/uploads`: Directory for storing user-uploaded media (created on first upload).
