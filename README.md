# Intelligent IT Support & Incident Management System

A full-stack, enterprise-grade **IT Support and Incident Management System** featuring **Automated Ticket Categorization**, **Priority Prediction**, **Cosine Similarity Duplicate Detection**, and **Role-Based Workflows**.

Designed as a final-year **B.Tech Computer Science project**, resume portfolio showcase, and technical interview demonstration.

---

## Key Features

1. **Role-Based Security & JWT Auth**:
   - **`EMPLOYEE`**: Report incidents, view own ticket timeline, add discussion comments.
   - **`SUPPORT_AGENT`**: Manage incident queues, assign tickets, transition status lifecycle, override priorities, view service analytics.
   - **`ADMIN`**: Provision & disable user accounts, modify role permissions, inspect global system telemetry.
2. **AI & Machine Learning Microservice**:
   - **Automated Category Prediction**: Multi-class NLP classifier trained on IT support dataset using TF-IDF text vectorization and Logistic Regression.
   - **Automated Priority Prediction**: Predicts ticket impact (LOW, MEDIUM, HIGH, CRITICAL) from text signals and keywords.
   - **Duplicate Incident Detection**: TF-IDF Cosine Similarity engine that compares new ticket submissions against existing incidents, flagging duplicates with similarity percentage and matching ticket numbers.
3. **Incident Audit Trail & Workflow Lifecycle**:
   - Status transition rules: `NEW` ➔ `ASSIGNED` ➔ `IN_PROGRESS` ➔ `RESOLVED` ➔ `CLOSED` (or `IN_PROGRESS` ➔ `REOPENED`).
   - Every status change, priority override, and assignment is recorded in the `incident_history` table for compliance audit trails.
4. **Interactive Dashboard & Analytics**:
   - Visual charts (Category Distribution, Priority Breakdown, Departmental Tickets, Agent Workload) powered by Chart.js.

---

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.x, Spring Data JPA, Spring Security, JWT (jjwt), Hibernate, Lombok, Bean Validation.
- **Frontend**: React 18, Vite, Tailwind CSS, Lucide Icons, Axios, React Router v6, Chart.js.
- **ML Microservice**: Python 3.11, FastAPI, Uvicorn, Scikit-Learn, Pandas, NumPy, Joblib.
- **Database**: MySQL / H2 In-Memory (Zero configuration setup for evaluation).

---

## Demo Credentials

All accounts use the default password: **`Password123!`**

| Role | Email | Password |
| :--- | :--- | :--- |
| **System Admin** | `admin@itsupport.com` | `Password123!` |
| **Support Agent Tier 2** | `agent.smith@itsupport.com` | `Password123!` |
| **Infrastructure Agent** | `agent.jones@itsupport.com` | `Password123!` |
| **Employee (Sales)** | `john.doe@enterprise.com` | `Password123!` |
| **Employee (HR)** | `jane.smith@enterprise.com` | `Password123!` |
| **Employee (Dev)** | `mike.wilson@enterprise.com` | `Password123!` |

---

## How to Run locally on Windows Laptop

### Step 1: Start the Python ML Service
```bash
cd ml-service
pip install -r requirements.txt
python train.py
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```
*The ML REST API will run at `http://localhost:8000`*

### Step 2: Start the Spring Boot Backend
```bash
cd backend
./mvnw spring-boot:run
```
*The Spring Boot REST API will run at `http://localhost:8080`. On initial startup, `DataSeeder` auto-seeds 8 users and 30+ sample IT tickets.*

### Step 3: Start the React Frontend
```bash
cd frontend
npm install
npm run dev
```
*Open `http://localhost:5173` in your browser.*

---

## One-Click Startup Script (Windows)

Double-click `run_all.bat` in the root directory to launch all services automatically!
