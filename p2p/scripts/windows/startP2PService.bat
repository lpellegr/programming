SETLOCAL enabledelayedexpansion
call ..\..\..\scripts\windows\init.bat

echo.
echo --- StartP2PService -------------------------------------

%JAVA_CMD% org.objectweb.proactive.p2p.core.service.StartP2PService %1 %2 %3

echo.

echo ---------------------------------------------------------