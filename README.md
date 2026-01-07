# Campus Pulse 🎓

> A robust Issue Tracker application designed to streamline campus problem reporting and management.

![Project Status](https://img.shields.io/badge/status-active-brightgreen)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)

## 📝 About The Project

**Campus Pulse** is a backend application developed to help educational institutions manage and resolve campus-related issues efficiently. It serves as a centralized platform where issues can be reported, tracked, and updated.

The system is built using the **Spring Boot** framework, ensuring scalability and rapid development, and follows a standard MVC architecture.

### 🌟 Key Features

* **Issue Reporting:** Create and log new issues with detailed descriptions and categories.
* **Status Tracking:** Monitor the lifecycle of an issue (e.g., Open, In Progress, Resolved).
* **RESTful API:** Fully functional API endpoints for integration with frontend applications.
* **Data Persistence:** Uses JPA/Hibernate for seamless database interaction with MySQL.

---

## 📂 Project Structure

The source code is located inside the `pulse` directory. Here is a guide to the key folders visible in the project:

| Component | Path | Description |
| :--- | :--- | :--- |
| **Controllers** | `pulse/src/main/java/com/campus/pulse/controller` | **(Start Here)** Contains the API endpoints (e.g., `IssueController.java`) that handle web requests. |
| **Models** | `pulse/src/main/java/com/campus/pulse/model` | Defines the data structure and database tables (e.g., `Issue.java`). |
| **Repositories** | `pulse/src/main/java/com/campus/pulse/repository` | Handles database connections and SQL queries. |
| **Main App** | `pulse/src/main/java/com/campus/pulse/CampuspolseApplication.java` | The entry point used to start the Spring Boot server. |
| **Config** | `pulse/src/main/resources/application.properties` | Database configuration and server settings. |

---

## 🛠️ Tech Stack

* **Language:** Java
* **Framework:** Spring Boot (Web, JPA)
* **Database:** MySQL
* **Build Tool:** Maven
* **IDE:** IntelliJ IDEA / VS Code / Eclipse

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

* JDK 17 or later
* Maven installed
* MySQL Server running locally

### Installation & Run

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/](https://github.com/)[your-username]/campus-pulse.git
    ```

2.  **Navigate to the project directory**
    ```bash
    cd campus-pulse
    ```

3.  **Configure the Database**
    * Create a database named `campus_pulse_db` in MySQL.
    * Update `src/main/resources/application.properties` with your credentials:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/campus_pulse_db
        spring.datasource.username=root
        spring.datasource.password=your_password
        spring.jpa.hibernate.ddl-auto=update
        ```

4.  **Build and Run**
    ```bash
    mvn spring-boot:run
    ```

---

## 📖 API Endpoints

The application exposes the following REST endpoints (running on `http://localhost:8080` by default):

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/issues` | Retrieve a list of all reported issues |
| `POST` | `/issues` | Report a new issue |
| `GET` | `/issues/{id}` | Get details of a specific issue by ID |
| `PUT` | `/issues/{id}` | Update an issue (e.g., change status) |
| `DELETE` | `/issues/{id}` | Delete an issue record |

### 🧪 Sample JSON Payload

**POST** `/issues` (Creating a new issue)
```json
{
  "title": "Projector not working in Lab 3",
  "description": "The HDMI cable seems to be broken.",
  "status": "OPEN",
  "priority": "HIGH"
}
