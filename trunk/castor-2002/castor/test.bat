@echo off
REM $Id$
set JAVA=%JAVA_HOME%\bin\java
set CLASSPATH=build\classes;build\examples;%CLASSPATH%
set cp=%CLASSPATH%
for %%i in (lib\*.jar) do call b.bat %%i
set CP=%CP%;%JAVA_HOME%\lib\tools.jar

%JAVA% -classpath %CP% %1.Test %2 %3 %4 %5 %6
