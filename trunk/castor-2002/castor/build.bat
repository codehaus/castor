@echo off
REM $Id$
set JAVA=%JAVA_HOME%\bin\java
for %%i in (lib\*.jar) do set CLASSPATH=%%i;%CLASSPATH%
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
%JAVA% -classpath %CLASSPATH% -Dant.home=lib org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 -buildfile src/build.xml

