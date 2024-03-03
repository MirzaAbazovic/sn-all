#!/bin/sh
CATALINA_HOME=${catalina.home}
CATALINA_BASE=${catalina.base}
CATALINA_OPTS="-Duse.config=${use.config} $CATALINA_OPTS"
test -n "$CATALINA_PID" || CATALINA_PID=$CATALINA_BASE/temp/tomcat.pid
export CATALINA_HOME CATALINA_BASE CATALINA_OPTS CATALINA_PID

$CATALINA_HOME/bin/startup.sh
