# Intelligent IT Support & Incident Management System

A full-stack **IT Support and Incident Management System (ITSM)** featuring **Natural Language Processing (NLP) Ticket Categorization**, **Priority Prediction**, **Cosine Similarity Duplicate Detection**, and **Role-Based Incident Lifecycle Workflows**.

Designed as a comprehensive **final-year B.Tech Computer Science project**, resume portfolio showcase, and technical interview demonstration.

---

## Architecture Diagram

```
+-------------------------------------------------------------------------+
|                           React 18 + Vite UI                            |
|             (Tailwind CSS, Lucide Icons, Chart.js, Axios)               |
+------------------------------------+------------------------------------+
                                     | HTTP REST (with Bearer JWT)
                                     v
+-------------------------------------------------------------------------+
|                    Spring Boot 3.2.x Java Backend                       |
|   - Security: Spring Security 6 + JWT Stateless Authentication          |
|   - Controllers: Auth, Incident, Dashboard, User, AI                    |
|   - Services: IncidentService, AuthService, DashboardService, AI        |
|   - Data Access: Spring Data JPA + Hibernate                            |
|   - Heuristic Fallback: In-memory Java Rule Engine                      |
+-------------------+-------------------------------+---------------------+
                    |                               |
       HTTP POST    v                               v  JDBC (JPA / SQL)
+-----------------------+              +----------------------------------+
| Python FastAPI ML Svc |              | Relational Database              |
| - Scikit-Learn TF-IDF |              | - Default: In-Memory H2          |
| - Logistic Regression |              | - Optional: MySQL 8.x            |
| - Cosine Similarity   |              | - Tables: users, incidents,      |
+-----------------------+              |   comments, incident_history     |
                                       +----------------------------------+
```

---

## Verified Technology Stack

| Layer | Framework / Library | Version | Role in Project |
| :--- | :--- | :--- | :--- |
| **Backend** | Java | 17 | Core programming language |
| **Backend** | Spring Boot | 3.2.3 | REST API framework, DI container |
| **Backend** | Spring Security | 6.x | Role-based authorization & filter chain |
| **Backend** | JJWT | 0.11.5 | Token creation, signing, parsing |
| **Backend** | Spring Data JPA / Hibernate | 3.2.x | ORM & database persistence |
| **Backend** | H2 Database / MySQL | — | Relational persistence |
| **Backend** | Lombok | 1.18.x | Reduces boilerplate (getters/setters/builders)|
| **Frontend** | React | 18.2 | Component-based UI framework |
| **Frontend** | Vite | 5.4 | Next-generation frontend bundler & dev server|
| **Frontend** | Tailwind CSS | 3.4 | Utility-first responsive styling |
| **Frontend** | Chart.js / react-chartjs-2 | 4.x | Real-time analytics charts |
| **Frontend** | Axios | 1.6 | HTTP client with JWT interceptors |
| **Frontend** | React Router DOM | 6.22 | Client-side routing with role guards |
| **Frontend** | Lucide React | 0.344 | Modern UI icons |
| **ML Service** | Python | 3.11+ | Microservice runtime |
| **ML Service** | FastAPI + Uvicorn | 0.110+ | Asynchronous high-performance REST API |
| **ML Service** | Scikit-Learn | 1.4+ | TF-IDF vectorizer & Logistic Regression |
| **ML Service** | Pandas & NumPy | — | Dataset preprocessing and matrix math |

---

## Core Features & Business Logic

1. **Role-Based Access Control (RBAC)**:
   - **`ROLE_EMPLOYEE`**: Submit incident tickets, auto-populate AI category/priority suggestions, check duplicates live, view own submitted tickets, participate in discussion threads.
   - **`ROLE_SUPPORT_AGENT`**: View the global incident queue, filter by category/priority/status, assign tickets to agents, transition incident statuses, override ticket priorities.
   - **`ROLE_ADMIN`**: Full administrative authority; provision new users, disable/enable user accounts, assign custom roles (`ROLE_EMPLOYEE`, `ROLE_SUPPORT_AGENT`, `ROLE_ADMIN`), inspect global analytics.

2. **Machine Learning & NLP Microservice**:
   - **Category Prediction**: Multiclass Logistic Regression classifier trained on IT support issues using unigram + bigram TF-IDF text features.
   - **Priority Estimation**: Predicts urgency levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) using problem descriptions and signal keywords.
   - **Cosine Similarity Duplicate Detection**: Compares submitted tickets against existing incidents and reference vectors. When similarity exceeds 0.65 (65%), an alert is shown with the matching ticket number.
   - **Java Heuristic Fallback**: If the Python service is offline, the Spring Boot backend automatically falls back to an internal Java keyword & Jaccard similarity engine without crashing.

3. **Strict Incident Lifecycle & Audit Trail**:
   - Supported status transitions:
     - `NEW` &rarr; `ASSIGNED` or `IN_PROGRESS`
     - `ASSIGNED` &rarr; `IN_PROGRESS`
     - `IN_PROGRESS` &rarr; `RESOLVED`
     - `RESOLVED` &rarr; `CLOSED` or `REOPENED`
     - `REOPENED` &rarr; `IN_PROGRESS`
   - Every status modification, priority override, and assignment change writes an immutable audit record to `incident_history`.

4. **Service Analytics & Metrics**:
   - Real-time metrics computed directly from database queries:
     - Incident distributions by category, priority, and department.
     - Support agent active workloads.
     - Average Mean Time to Resolution (MTTR) in hours.

---

## Demo Credentials

All seeded accounts share the default password: **`Password123!`**

| Role | Email | Password | Intended Workflow |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@itsupport.com` | `Password123!` | User management, global incident queue, analytics |
| **Support Agent (Tier 2)** | `agent.smith@itsupport.com` | `Password123!` | Queue triage, ticket assignment, status updates |
| **Infrastructure Agent** | `agent.jones@itsupport.com` | `Password123!` | Network/server tickets, priority overrides |
| **Employee (Sales)** | `john.doe@enterprise.com` | `Password123!` | Create tickets, view personal ticket timeline |
| **Employee (HR)** | `jane.smith@enterprise.com` | `Password123!` | Report office issues, add comments |
| **Employee (Engineering)** | `mike.wilson@enterprise.com` | `Password123!` | Report developer workstation issues |

---

## Running the Application Locally (Windows)

### Option A: One-Click Startup (Recommended)
Double-click **`run_all.bat`** in the project root. It will open three terminal windows:
1. Python FastAPI ML microservice on port `8000`.
2. Spring Boot backend on port `8080`.
3. React Vite frontend on port `5173`.

### Option B: Manual Step-by-Step

#### 1. Start Python ML Microservice
```bash
cd ml-service
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
python train.py
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```
*Health check URL: `http://localhost:8000/health`*

#### 2. Start Spring Boot Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run
```
*API base URL: `http://localhost:8080`*  
*On startup, `DataSeeder` automatically creates 8 demo accounts and 30 realistic IT support incidents.*

#### 3. Start React Frontend
```bash
cd frontend
npm install
npm run dev
```
*Open `http://localhost:5173` in your web browser.*

---

## Database Configuration

By default, the project runs with an embedded **H2 in-memory database** (`jdbc:h2:mem:itsupportdb`), requiring zero setup or installation.

### Switching to MySQL
To use an external MySQL server, update `backend/src/main/resources/application.properties`:
```properties
# Uncomment MySQL properties:
spring.datasource.url=jdbc:mysql://localhost:3306/itsupport_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```
Create the database in MySQL before running:
```sql
CREATE DATABASE itsupport_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## Running Automated Tests

Run the backend test suite:
```bash
cd backend
.\mvnw.cmd test
```

### Verified Test Cases:
- **`AuthServiceTest`**:
  - `testRegisterEnforcesEmployeeRole`: Confirms public registration forces `ROLE_EMPLOYEE`.
  - `testRegisterDuplicateEmailThrowsException`: Verifies duplicate email handling.
  - `testGetUserByIdFound` & `testGetUserByIdNotFound`: Validates user retrieval and 404 behavior.
- **`IncidentServiceTest`**:
  - `testCreateIncidentGeneratesCorrectTicketNumber`: Validates deterministic `INC-(1000 + ID)` generation.
  - `testEmployeeCannotOverridePriority`: Confirms permission boundary for employees.
  - `testInvalidStatusTransitionThrowsBadRequest`: Rejects invalid transitions (e.g. `NEW` &rarr; `CLOSED`).
  - `testValidStatusTransitionSucceeds`: Verifies state change and history audit record creation.
- **`ItSupportApplicationTests`**:
  - `contextLoads`: Validates full Spring application context initialization.

To build the React production bundle:
```bash
cd frontend
npm run build
```

---

## Known Design Decisions & Limitations

1. **Dual AI Architecture**: The Python ML microservice performs genuine TF-IDF + Logistic Regression inference. If it is unreachable, the Java backend seamlessly falls back to a rule-based NLP parser, ensuring high availability.
2. **Public Self-Registration Security**: Self-registration via `POST /api/auth/register` strictly forces `ROLE_EMPLOYEE`. Support agents and administrators can only be created by an authenticated administrator.
3. **Audit History Immutability**: Records in `incident_history` have `updatable = false` on timestamps to maintain integrity.
4. **H2 In-Memory Reset**: Every restart of Spring Boot repopulates clean demo data via `DataSeeder`. Switching to MySQL persists data permanently across restarts.
