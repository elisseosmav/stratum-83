@echo off
setlocal
cd /d "%~dp0"

set "MAVEN_CMD="
where mvn.cmd >nul 2>nul
if not errorlevel 1 set "MAVEN_CMD=mvn.cmd"

if not defined MAVEN_CMD if exist "%USERPROFILE%\Projects\Games\apache-maven\" (
    for /r "%USERPROFILE%\Projects\Games\apache-maven" %%F in (mvn.cmd) do (
        if not defined MAVEN_CMD set "MAVEN_CMD=%%~fF"
    )
)

if not defined MAVEN_CMD (
    echo Maven could not be found.
    echo Install Maven or add its bin directory to PATH.
    echo Alternatively, place Maven under Projects\Games\apache-maven.
    pause
    exit /b 1
)

echo Starting STRATUM-83...
call "%MAVEN_CMD%" javafx:run
if errorlevel 1 (
    echo.
    echo STRATUM-83 did not start. Check the error messages above.
    pause
    exit /b 1
)
exit /b 0
