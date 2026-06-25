@echo off
title M.R.GRAY BOARDS - Update System
color 0E
setlocal enabledelayedexpansion

echo ========================================
echo    M.R.GRAY BOARDS - Update System
echo ========================================
echo.

REM Stop running servers first
echo [1/4] Stopping existing servers...
taskkill /F /FI "WINDOWTITLE eq M.R.GRAY*" >nul 2>&1
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo Done.
echo.

set BE_PATH=%~dp0
set FE_PATH=%~dp0..\invoice-FE

REM Git pull latest code
echo [2/4] Pulling latest code from GitHub...
cd /d "%BE_PATH%"
git pull
cd /d "%FE_PATH%"
git pull
cd /d "%BE_PATH%"
echo Done.
echo.

REM Rebuild backend JAR
echo [3/4] Building backend...
cd /d "%BE_PATH%"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo Done.
echo.

REM Update frontend dependencies
echo [4/4] Updating frontend dependencies...
cd /d "%FE_PATH%"
call npm install
cd /d "%BE_PATH%"
echo Done.
echo.

echo ========================================
echo   Update complete! Starting system...
echo ========================================
timeout /t 2 /nobreak >nul

call "%BE_PATH%Start System.bat"
