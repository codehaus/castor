@echo off
REM $Id$
set JAVA=%JAVA_HOME%\bin\java
set OLDCP=%CLASSPATH%
set CLASSPATH=build\classes;build\tests;%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
set cp=%CLASSPATH%
for %%i in (lib\*.jar) do call cp.bat %%i
deltree /Y .\build\tests\output
%JAVA% -classpath %CP% org.exolab.castor.tests.framework.CastorTestSuiteRunner %1 %2 %3  %4 %5 %6 %7
set CLASSPATH=%OLDCP%
