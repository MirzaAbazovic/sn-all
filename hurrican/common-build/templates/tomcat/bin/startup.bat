set CATALINA_HOME=${webapp.install.path}\apache-tomcat-${tomcat.revision}
set CATALINA_BASE=${webapp.install.path}\instance
set CATALINA_OPTS=-Duse.config=${use.config}
set JAVA_OPTS=%JAVA_OPTS% ${tomcat.java.opts} -Dcom.sun.management.snmp.interface=${host.name} -Dcom.sun.management.snmp.port=${tomcat.snmp.port} -Dcom.sun.management.snmp.acl=false

call "%CATALINA_HOME%\bin\startup.bat"