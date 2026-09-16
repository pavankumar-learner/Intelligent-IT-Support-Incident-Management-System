# REST API Documentation — Intelligent IT Support & Incident Management System

All API requests expecting authentication require the header:
`Authorization: Bearer <JWT_TOKEN>`

---

## 1. Authentication APIs

### Login
`POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "john.doe@enterprise.com",
  "password": "Password123!"
}
```
- **Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 4,
  "name": "John Doe",
  "email": "john.doe@enterprise.com",
  "role": "ROLE_EMPLOYEE",
  "department": "Sales & Marketing"
}
```

### Register User
`POST /api/auth/register`
- **Request Body**:
```json
{
  "name": "Alex Johnson",
  "email": "alex.johnson@enterprise.com",
  "password": "Password123!",
  "role": "ROLE_EMPLOYEE",
  "department": "Engineering"
}
```

---

## 2. Incident Management APIs

### Create Incident
`POST /api/incidents`
- **Request Body**:
```json
{
  "title": "Unable to connect to Wi-Fi in Block B",
  "description": "My laptop drops Wi-Fi connection every 10 minutes.",
  "category": "Network",
  "priority": "HIGH",
  "location": "Block B, Desk 301",
  "department": "Sales & Marketing"
}
```

### Get All Incidents (Filtered)
`GET /api/incidents?search=wifi&category=Network&priority=HIGH&status=NEW`

### Assign Incident to Support Agent
`PUT /api/incidents/{id}/assign`
- **Request Body**: `{"agentId": 2}`

### Update Incident Status
`PUT /api/incidents/{id}/status`
- **Request Body**:
```json
{
  "status": "RESOLVED",
  "comment": "Rebooted access point AP-04 in Block B. Connection stabilized."
}
```

### Add Comment
`POST /api/incidents/{id}/comments`
- **Request Body**: `{"comment": "Checking signal strength on channel 6."}`

---

## 3. AI Prediction REST APIs

### Predict Category
`POST /api/ai/predict-category`
- **Request Body**:
```json
{
  "title": "Printer spooler service crashed on 3rd floor",
  "description": "HP LaserJet paper jam and offline error."
}
```
- **Response**:
```json
{
  "prediction": "Printer",
  "confidence": 0.92,
  "topKeywords": ["printer", "spooler", "paper"]
}
```

### Check Duplicate Incident
`POST /api/ai/check-duplicate`
- **Request Body**:
```json
{
  "title": "Unable to connect to Wi-Fi network in Block B",
  "description": "Wi-Fi connection failing.",
  "threshold": 0.65
}
```
- **Response**:
```json
{
  "isDuplicate": true,
  "similarityScore": 88.5,
  "matchedTicketNumber": "INC-1001",
  "matchedTitle": "Unable to connect to office Wi-Fi network in Block B",
  "message": "Potential duplicate incident detected (88.5% similarity with INC-1001)."
}
```
