set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_31

call mvn clean
call mvn package
call mvn_install.cmd
rem START call rservice_start.cmd
