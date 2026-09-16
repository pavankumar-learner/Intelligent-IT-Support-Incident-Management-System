# Database Schema Documentation — Intelligent IT Support & Incident Management System

The database schema consists of four relational tables:

```
+----------------+          +--------------------+          +-----------------------+
|     users      |          |     incidents      |          |   incident_comments   |
+----------------+          +--------------------+          +-----------------------+
| PK id          |<--------+| PK id              |<--------+| PK id                 |
|    email (UQ)  | created  |    ticket_no (UQ)  |          | FK incident_id        |
|    password    | by /     |    title           |          | FK user_id            |
|    role        | assigned |    category        |          |    comment            |
|    department  | to       |    priority        |          |    created_at         |
|    active      |          |    status          |          +-----------------------+
+----------------+          | FK created_by      |
                            | FK assigned_to     |          +-----------------------+
                            +--------------------+          |   incident_history    |
                                      |                     +-----------------------+
                                      +--------------------+| PK id                 |
                                                            | FK incident_id        |
                                                            | FK changed_by         |
                                                            |    old_status         |
                                                            |    new_status         |
                                                            |    old_priority       |
                                                            |    new_priority       |
                                                            |    action_note        |
                                                            |    changed_at         |
                                                            +-----------------------+
```

## Tables Overview

1. **`users`**:
   - Stores account profile information, encrypted BCrypt passwords, role assignments (`ROLE_EMPLOYEE`, `ROLE_SUPPORT_AGENT`, `ROLE_ADMIN`), and active account status flags.

2. **`incidents`**:
   - Stores incident details, generated ticket numbers (`INC-1001`), user selected vs AI predicted category/priority values, confidence metrics, lifecycle status (`NEW`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `REOPENED`), foreign keys to reporter and assigned agent.

3. **`incident_comments`**:
   - Stores threaded discussion notes between employees and IT support agents.

4. **`incident_history`**:
   - Stores state transition audit history for compliance and SLA tracking.
