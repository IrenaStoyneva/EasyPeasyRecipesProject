# EasyPeasyRecipesProject
This is my first personal Spring MVC project application for EasyPeasyRecipes. It provides functionalities for managing and sharing recipes.

## Description

EasyPeasyRecipes is a web application that allows users to browse, create, and share recipes. Users can also add comments and rate recipes. The application integrates with a REST API to manage comments.

## Download

[![Download ZIP](https://img.shields.io/badge/Download-ZIP-blue)](https://github.com/IrenaStoyneva/EasyPeasyRecipesProject/releases/download/v1.0.1/EasyPeasyRecipes-master.zip)


## Setting Up Environment Variables

Before running the application, make sure to set up the following environment variables. You can do this by creating a `.env` file in the root directory of the project.

```plaintext
JWT_KEY=your_jwt_key_here
DB_URL=jdbc:mysql://localhost:3306/easypeasyrecipes
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
ADMIN_USERNAME=your_admin_username
ADMIN_PASSWORD=your_admin_password
ADMIN_EMAIL=your_admin_email@example.com
```

## JWT Authentication
Overview
EasyPeasyRecipes uses JWT (JSON Web Token) for secure user authentication. After a successful login, a JWT is generated and used to access protected resources within the application.

## Technologies Used

- Java 22
- Spring Boot 2.0
- Spring Security
- Spring Data JPA
- Thymeleaf
- HTML, CSS, JavaScript
- MySQL
- RestClient

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java 21 or later installed
- MySQL database running

 # Comments Recipes API

This project provides a RESTful API for managing comments on recipes.

## API Endpoints

### 1. Create a New Comment

- **Endpoint:** `POST http://localhost:8081/api/comments/recipe/{recipeId}`
- **Description:** Create a new comment for a specific recipe.
- **Request Body:**
  ```json
  {
      "content": "This is a test comment.",
      "createdOn": "2024-08-13T12:00:00",
      "authorId": 1,
      "recipeId": 5
  }
## 2. Update comment
- **Endpoint:** `PUT http://localhost:8081/api/comments/{commentId}`
- - **Request Body:**
```json
{
    "content": " Тest comment.",
    "createdOn": "2024-08-13T12:00:00",
    "authorId": 1,
    "recipeId": 5
}


