@echo off
REM $Id$
set JAVA=%JAVA_HOME%\bin\java
set CLASSPATH=build\classes;build\examples;%CLASSPATH%
for %%i in (lib\*.jar) do set CLASSPATH=%%i;%CLASSPATH%
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

%JAVA% -classpath %CLASSPATH% %1.Test %2 %3 %4 %5 %6
