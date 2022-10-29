@echo off

color 4

call :check_requirement mvn Maven https://maven.apache.org/download.cgi
if "%missing_requirement%" == "true" (
  echo.
  PAUSE
  exit 2
)

set "missing_file=false"
set "go_back=false"
call :check_file pom.xml 1
if "%missing_file%" == "true" (
  echo.
  PAUSE
  exit 2
)
if "%go_back%" == "true" (
  cd ..
)

color b
echo All dependencies found
echo Selecting update mode...
echo.

set /a script_type=0
for %%x in (%*) do (
  if "%%x" == "--minor" set /a script_type=1
  if "%%x" == "--major" set /a script_type=2
)


echo Reading project version...
set mvn_version=Not available
cmd /c "mvn help:evaluate -f %cd%\pom.xml -Dexpression=project.version -q -DforceStdout" > tmp_output
color b
set /p mvn_version= < tmp_output
del /f tmp_output
echo Current project version: %mvn_version%
echo.

set v_fix=0
set v_minor=0
set v_major=0
set v_length=0

set string=%mvn_version%
:split_loop
FOR /f "tokens=1* delims=." %%i IN ("%string%") DO (
  set /a v_length = %v_length% + 1
  if %v_length% == 3 (
    cls
    color 4
    echo.
    echo Unsupported version!
    echo.
echo %~dp0
    echo Version: %mvn_version%
    echo Size: %v_length% of 3
    echo.
    PAUSE
    exit 4
  )
  if %v_length% == 0 set /a v_major=%%i
  if %v_length% == 1 set /a v_minor=%%i
  if %v_length% == 2 set /a v_fix=%%i
  set string=%%j
  if defined string (
    set string=%%j
    goto split_loop
  ) else (
    set string=%%i
  )
)
if not %v_length% == 3 (
  cls
  color 4
  echo.
  echo Unsupported version!
  echo.
echo %~dp0
  echo Version: %mvn_version%
  echo Size: %v_length% of 3
  echo.
  PAUSE
  exit 4
)

if "%script_type%" == "0" goto fix
if "%script_type%" == "1" goto minor
if "%script_type%" == "2" goto major

echo Unknown type - How???
PAUSE
exit 3
goto :eof

:fix
echo Updating fix version
set /a v_fix = %v_fix% + 1
goto mvn_update

:minor
echo Updating minor version
set /a v_fix = 0
set /a v_minor = %v_minor% + 1
goto mvn_update

:major
echo Updating major version
set /a v_fix = 0
set /a v_minor = 0
set /a v_major = %v_major% + 1
goto mvn_update

:mvn_update
set set_version=%v_major%.%v_minor%.%v_fix%
echo.
echo New version: %set_version%
echo.
cmd /c "mvn versions:set -f %cd%\pom.xml -DnewVersion=%set_version%"
cmd /c "mvn versions:update-child-modules"
del pom.xml.versionsBackup
echo.
echo.
echo.
echo Successfully updated from %mvn_version% to %set_version%!
echo.
echo.
PAUSE
exit 5
goto :eof


:: Routines
:check_requirement
if "%missing_requirement%" == "true" (
  exit /b
)
where %1 > NUL 2>&1 && exit /b
echo Download and install %2 from %3
set "missing_requirement=true"
exit /b

:check_file
if "%missing_file%" == "true" (
  exit /b
)
if not %1 == NUL if exist %cd%\%1 (
  exit /b
)
if not %1 == NUL if not %2 == NUL if %2 == 1 if exist %cd%\..\%1 (
  set "go_back=true"
  exit /b
)
set "missing_file=true"
echo Couldn't find file %1
exit /b