@echo off
cd /d "%~dp0"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven is not installed or is not on PATH.
  echo See README.md for installation instructions.
  pause
  exit /b 1
)
echo Starting STRATUM-83...
call mvn javafx:run
if errorlevel 1 pause
