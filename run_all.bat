@echo off
title Launching Intelligent IT Support ^& Incident Management System
echo =======================================================================
echo    Launching Intelligent IT Support ^& Incident Management System
echo =======================================================================

echo.
echo [1/3] Starting Python ML Microservice on port 8000...
start "NexusIT - ML Microservice" /D "%~dp0ml-service" cmd /k ""%~dp0.venv\Scripts\python.exe" -m uvicorn app:app --host 0.0.0.0 --port 8000 --reload"

echo.
echo [2/3] Starting Spring Boot Backend on port 8080...
start "NexusIT - Spring Boot Backend" /D "%~dp0backend" cmd /k ".\mvnw.cmd spring-boot:run"

echo.
echo [3/3] Starting React Frontend on port 5173...
start "NexusIT - React Frontend" /D "%~dp0frontend" cmd /k "npm run dev"

echo.
echo =======================================================================
echo All services launched!
echo Access Frontend UI at: http://localhost:5173
echo Login Credentials:
echo   - Employee:      john.doe@enterprise.com   / Password123!
echo   - Support Agent: agent.smith@itsupport.com / Password123!
echo   - System Admin:  admin@itsupport.com       / Password123!
echo =======================================================================
pause
