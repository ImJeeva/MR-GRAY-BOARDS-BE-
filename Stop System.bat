@echo off
title Stop M.R.GRAY BOARDS
color 0C

echo ========================================
echo    Stopping Servers
echo ========================================
echo.

echo Stopping Backend (Java)...
taskkill /F /FI "WINDOWTITLE eq M.R.GRAY*" 2>nul
taskkill /F /IM java.exe 2>nul

echo Stopping Frontend (Node)...
taskkill /F /IM node.exe 2>nul

echo.
echo ========================================
echo    All servers stopped!
echo ========================================
timeout /t 2 >nul
