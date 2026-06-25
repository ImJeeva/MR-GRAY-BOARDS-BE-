@echo off
title M.R.GRAY BOARDS - Invoice System
color 0B
setlocal enabledelayedexpansion

echo ========================================
echo    M.R.GRAY BOARDS - Invoice System
echo ========================================
echo.

REM BE is current folder, FE is sibling folder
set BE_PATH=%~dp0
set FE_PATH=%~dp0..\invoice-FE

REM Load config.properties
set CONFIG=%BE_PATH%config.properties
if not exist "%CONFIG%" (
    echo ERROR: config.properties not found!
    pause
    exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%A in ("%CONFIG%") do (
    set "key=%%A"
    set "val=%%B"
    if not "!key:~0,1!"=="#" if not "!key!"=="" (
        set "!key!=!val!"
    )
)

echo DB Host     : %DB_HOST%
echo DB Port     : %DB_PORT%
echo DB Name     : %DB_NAME%
echo DB Username : %DB_USERNAME%
echo.

REM Check Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 17 or higher.
    pause
    exit /b 1
)

REM Check Node
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js not found! Please install Node.js.
    pause
    exit /b 1
)

REM Auto install frontend dependencies if missing
if not exist "%FE_PATH%\node_modules" (
    echo [Setup] Installing frontend dependencies, please wait...
    cd /d "%FE_PATH%"
    call npm install
    cd /d "%BE_PATH%"
    echo.
)

REM Build JAR if missing
set JAR_PATH=%BE_PATH%target\invoice-0.0.1-SNAPSHOT.jar
if not exist "%JAR_PATH%" (
    echo [Setup] Building backend, please wait...
    where mvn >nul 2>&1
    if %errorlevel% neq 0 (
        echo ERROR: Maven not found. Please install Maven or contact developer.
        pause
        exit /b 1
    )
    cd /d "%BE_PATH%"
    call mvn clean package -DskipTests
    echo.
)

echo Starting Backend Server...
start "M.R.GRAY BACKEND" cmd /k "java -jar "%JAR_PATH%" --DB_HOST=%DB_HOST% --DB_PORT=%DB_PORT% --DB_NAME=%DB_NAME% --DB_USERNAME=%DB_USERNAME% --DB_PASSWORD=%DB_PASSWORD%"

timeout /t 8 /nobreak >nul

echo Starting Frontend...
start "M.R.GRAY FRONTEND" cmd /k "cd /d "%FE_PATH%" && npm run dev"

echo.
echo ========================================
echo   Both servers are starting...
echo.
echo   Backend:  http://localhost:8080
echo   Frontend: http://localhost:5173
echo.
echo   Open browser: http://localhost:5173
echo   Login: admin / admin123
echo.
echo   Press any key to close this window...
echo ========================================
pause >nul
