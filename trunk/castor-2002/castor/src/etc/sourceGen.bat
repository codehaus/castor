@echo off
set castor_cp=%CLASSPATH%

REM Batch files allow up to 9 parameters (%0 is the command name)
set castor_cmdline=%1 %2 %3 %4 %5 %6 %7 %8 %9

java -classpath %castor_cp% org.exolab.castor.builder.SourceGenerator %castor_cmdline%

REM clean up variables
set castor_cp=
set castor_cmdline=