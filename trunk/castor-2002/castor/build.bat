# $Id$
@echo off
set JAVA=%JAVA_HOME%\bin\java
for %%LIB in (lib\*.jar) do set CLASSPATH=%%LIB;%CLASSPATH%
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

%JAVA% -classpath %CLASSPATH% -Dant.home=lib org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 -buildfile src/build.xml

