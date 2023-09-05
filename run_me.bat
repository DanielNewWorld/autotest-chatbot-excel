@echo off

set PATH=%CD%\apache-maven\bin\;%CD%\jdk-17\bin;%PATH%

echo %Path%
java -version
apache-maven\bin\mvn clean package