set CATALINA_HOME=${webapp.install.path}\apache-tomcat-${tomcat.revision}
set CATALINA_BASE=${webapp.install.path}\instance

call %CATALINA_HOME%\bin\shutdown.bat -force