@echo off
echo. 
echo --- Fractal HelloWorld example ----------------------------------------
rem if "%1" == "help" goto usage

goto doit

:usage
echo. 
echo helloworld-fractal.sh <optional parameters>
echo		
echo		parameters are :
echo			- parser
echo			- wrapper
echo			- distributed (needs parser)  echo. 
goto doit


:doit
IF NOT DEFINED PROACTIVE set PROACTIVE=..\..\..\.
SETLOCAL
call %PROACTIVE%\scripts\windows\init.bat
set JAVA_CMD=%JAVA_CMD% -Dfractal.provider=org.objectweb.proactive.core.component.Fractive
%JAVA_CMD%  org.objectweb.proactive.examples.components.helloworld.HelloWorld %1 %2 %3 
ENDLOCAL

:end
echo. 
echo ---------------------------------------------------------
