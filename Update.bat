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

REM Git pull latest code
echo [2/4] Pulling latest code from GitHub...
cd /d "%~dp0invoice-BE"
git pull
cd /d "%~dp0invoice-FE"
git pull
cd /d "%~dp0"
echo Done.
echo.

REM Rebuild backend JAR
echo [3/4] Building backend...
cd /d "%~dp0invoice-BE"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
cd /d "%~dp0"
echo Done.
echo.

REM Update frontend dependencies
echo [4/4] Updating frontend dependencies...
cd /d "%~dp0invoice-FE"
call npm install
cd /d "%~dp0"
echo Done.
echo.

echo ========================================
echo   Update complete! Starting system...
echo ========================================
timeout /t 2 /nobreak >nul

call "%~dp0Start System.bat"
