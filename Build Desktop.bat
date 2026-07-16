@echo off
title Build M.R.GRAY BOARDS Desktop App
color 0A

REM BE is current folder, FE is sibling folder
set BE_PATH=%~dp0
set FE_PATH=%~dp0..\invoice-FE

echo ========================================
echo    Building M.R.GRAY BOARDS Desktop App
echo ========================================
echo.

echo [1/4] Building Frontend (React)...
cd /d "%FE_PATH%"
call npm run build
if %errorlevel% neq 0 (
    echo ERROR: Frontend build failed!
    pause
    exit /b 1
)
echo.

echo [2/4] Copying to Backend...
if exist "%BE_PATH%src\main\resources\static" rmdir /s /q "%BE_PATH%src\main\resources\static"
mkdir "%BE_PATH%src\main\resources\static"
xcopy /e /y /q "%FE_PATH%\dist\*" "%BE_PATH%src\main\resources\static\"
echo.

echo [3/4] Building Backend (Spring Boot JAR)...
cd /d "%BE_PATH%"
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo ERROR: Backend build failed!
    pause
    exit /b 1
)
echo.

echo [4/4] Creating Single Desktop Executable...
copy /y "%BE_PATH%target\invoice-0.0.1-SNAPSHOT.jar" "%BE_PATH%backend.jar" >nul

set ICON_FLAG=
if exist "%BE_PATH%icon.ico" set ICON_FLAG=/win32icon:"%BE_PATH%icon.ico"
@"%SystemRoot%\Microsoft.NET\Framework\v4.0.30319\csc.exe" /nologo /target:winexe %ICON_FLAG% /r:System.Windows.Forms.dll /r:System.Drawing.dll /resource:"%BE_PATH%backend.jar" /resource:"%BE_PATH%config.properties" /out:"%BE_PATH%MR Gray Boards.exe" "%BE_PATH%Launcher.cs"

if exist "%BE_PATH%MR Gray Boards.exe" (
    del "%BE_PATH%backend.jar"
    echo.
    echo ========================================
    echo    Build Complete!
    echo.
    echo    Single file ready:
    echo      MR Gray Boards.exe
    echo.
    echo    Put this ONE file on the desktop.
    echo    Double-click to start the app.
    echo.
    echo    Requirements: Java 17+, MySQL server running
    echo ========================================
) else (
    echo ERROR: Could not create executable!
    pause
    exit /b 1
)
pause
