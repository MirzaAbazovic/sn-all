#!/bin/bash

# Aufruf aus Hudson, wenn ak-hurrican-web/src/deploy ausgecheckt wurde
#
# # prevent Hudson killing spawned Tomcat process
# export BUILD_ID=dontKillMe
# export JAVA_OPTS="-Duse.config=usertest"
# ./deploy/deploy_hurricanweb.sh -t /home/tomcat/tomcat-usertest -w $TESTRELEASE_WORKSPACE/../lastStable/archive/trunk/ak-hurrican-web/dist/hurricanweb_testing_$TESTRELEASE_BUILD_NUMBER.war


readopts() {
  while getopts "t:w:" optname
    do
      case "$optname" in
        "t")
          TOMCAT_DIR=$OPTARG
          echo using tomcat dir $TOMCAT_DIR
          ;;
        "w")
          WARFILE=$OPTARG
          echo using warfile $WARFILE
          ;;
      esac
    done

  if [ x$TOMCAT_DIR = x ]; then
    echo "no TOMCAT_DIR location given"
    exit 1
  fi

  if [ x$WARFILE = x ]; then
    echo "no WARFILE location given"
    exit 1
  fi
}

shutdown_tc () {
  echo "shutting down Tomcat"
  $TOMCAT_DIR/bin/shutdown.sh
  sleep 5
}

startup_tc() {
  echo "starting up Tomcat"
  $TOMCAT_DIR/bin/startup.sh
}

cleanup() {
  rm -rf $TOMCAT_DIR/work/*
  rm -rf $TOMCAT_DIR/temp/*
  rm -rf $TOMCAT_DIR/webapps/hurricanweb
  rm -f $TOMCAT_DIR/webapps/hurricanweb.war
}

deploy_war() {
  WARFILE_BASENAME=$(basename $WARFILE)
  cp $WARFILE $TOMCAT_DIR/webapps-archive
  ln -s $TOMCAT_DIR/webapps-archive/$WARFILE_BASENAME $TOMCAT_DIR/webapps/hurricanweb.war
}

readopts "$@"
shutdown_tc
cleanup
deploy_war
startup_tc

