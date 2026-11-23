@echo off
cd /d "%~dp0"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven is not installed or is not on PATH.
  pause
  exit /b 1
)
call mvn test
if errorlevel 1 pause
