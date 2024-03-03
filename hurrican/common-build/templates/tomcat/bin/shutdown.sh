#!/bin/sh
CATALINA_HOME=${catalina.home}
CATALINA_BASE=${catalina.base}
test -n "$CATALINA_PID" || CATALINA_PID=$CATALINA_BASE/temp/tomcat.pid
export CATALINA_HOME CATALINA_BASE CATALINA_PID

$CATALINA_HOME/bin/shutdown.sh -force
