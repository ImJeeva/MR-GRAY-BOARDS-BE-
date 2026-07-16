@echo off
title M.R.GRAY BOARDS - Invoice System
color 0B

setlocal enabledelayedexpansion

echo ========================================
echo    M.R.GRAY BOARDS - Invoice System
echo ========================================
echo.

set BE_PATH=%~dp0
set CONFIG=%BE_PATH%config.properties
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=invoice_db
set DB_USERNAME=root
set DB_PASSWORD=

if exist "%CONFIG%" (
    for /f "usebackq tokens=1,* delims==" %%A in ("%CONFIG%") do (
        set "key=%%A"
        set "val=%%B"
        if not "!key:~0,1!"=="#" if not "!key!"=="" (
            set "!key!=!val!"
        )
    )
)

echo DB Host    : %DB_HOST%
echo DB Port    : %DB_PORT%
echo DB Name    : %DB_NAME%
echo DB Username: %DB_USERNAME%
echo.

if not exist "%BE_PATH%backend.jar" (
    echo ERROR: backend.jar not found!
    echo Please run Build Desktop.bat first.
    pause
    exit /b 1
)

echo Starting Server...
start "M.R.GRAY BOARDS" cmd /k "java -jar "%BE_PATH%backend.jar" --DB_HOST=%DB_HOST% --DB_PORT=%DB_PORT% --DB_NAME=%DB_NAME% --DB_USERNAME=%DB_USERNAME% --DB_PASSWORD=%DB_PASSWORD%"

timeout /t 8 /nobreak >nul

echo Opening browser...
start http://localhost:8080

echo.
echo ========================================
echo   App is running at: http://localhost:8080
echo   Close the server window to stop.
echo ========================================
exit
