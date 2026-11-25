@echo off
setlocal
cd /d "%~dp0"

set "MAVEN_CMD="

for /f "delims=" %%F in ('where.exe mvn.cmd 2^>nul') do (
    if not defined MAVEN_CMD if exist "%%~fF" set "MAVEN_CMD=%%~fF"
)

if not defined MAVEN_CMD (
    set "LOCAL_MAVEN=%USERPROFILE%\Projects\Games\apache-maven"
    if exist "%USERPROFILE%\Projects\Games\apache-maven\bin\mvn.cmd" (
        set "MAVEN_CMD=%USERPROFILE%\Projects\Games\apache-maven\bin\mvn.cmd"
    ) else (
        if exist "%USERPROFILE%\Projects\Games\apache-maven\" (
            for /r "%USERPROFILE%\Projects\Games\apache-maven" %%F in (mvn.cmd) do (
                if exist "%%~fF" if not defined MAVEN_CMD set "MAVEN_CMD=%%~fF"
            )
        )
    )
)

if not defined MAVEN_CMD (
    echo.
    echo Maven was not found. No game was started.
    echo Expected a real bin\mvn.cmd inside Projects\Games\apache-maven,
    echo or an mvn.cmd available on PATH.
    echo.
    echo Check the Maven folder or install Apache Maven.
    pause
    exit /b 1
)

echo Using Maven: %MAVEN_CMD%
call "%MAVEN_CMD%" -version
if errorlevel 1 (
    echo.
    echo Maven could not start. Verify the Java 21 installation.
    pause
    exit /b 1
)

echo.
echo Starting STRATUM-83...
call "%MAVEN_CMD%" javafx:run
if errorlevel 1 (
    echo.
    echo STRATUM-83 did not start. Review the errors above.
    pause
    exit /b 1
)
exit /b 0
