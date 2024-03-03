#!/bin/bash

# Script fuer die Verwaltung einer Tomcat Instanz fuer Hurrican bzw. Hurrican Fassade.

# exit script if any statement exits with non-zero or any uninitialized var is referenced
set -o errexit
set -o nounset


function stop-tomcat-initscript() {
    if [ -f /etc/init.d/${webapp.service.name} ] && [ -d ${catalina.base} ] ; then
        BUILD_ID=dontKillMe
        /usr/bin/sudo /etc/init.d/${webapp.service.name} stop
    fi
}

function start-tomcat-initscript() {
    BUILD_ID=dontKillMe
    /usr/bin/sudo /etc/init.d/${webapp.service.name} start
}

function status-tomcat-initscript() {
    /usr/bin/sudo /etc/init.d/${webapp.service.name} status
}

function clean_tomcat() {
    if [ -d ${webapp.install.path} ] ; then
        echo "cleaning ${webapp.install.path}"
        for DIR in ${webapp.install.path}/* ; do
            if [ `basename $DIR` != "logs" ] ; then
                    echo "delete $DIR"
                    /usr/bin/sudo rm -rf $DIR
            fi
        done
    fi

    # TODO test only
    # /usr/bin/sudo rm -rf ${webapp.install.path}
}

function deploy() {
    stop-tomcat-initscript
    clean_tomcat

    WORK_DIR=`pwd`
    TARGET_SUFFIX=${tomcat.user}

    echo "WORK_DIR='$WORK_DIR' TARGET_SUFFIX='$TARGET_SUFFIX'"


    # copy
    test ! -d ${webapp.install.path} && mkdir ${webapp.install.path}
    echo "start copy: cp -r $WORK_DIR/deployment/target/tomcat-$TARGET_SUFFIX-${use.config}/${webapp.service.name}/* ${webapp.install.path}"
    cp -r $WORK_DIR/deployment/target/tomcat-$TARGET_SUFFIX-${use.config}/${webapp.service.name}/* ${webapp.install.path}

    test ! -d ${webapp.install.path}/logs && mkdir ${webapp.install.path}/logs
    ln -s ${webapp.install.path}/logs ${catalina.base}/logs
    test ! -d ${catalina.base}/work && mkdir ${catalina.base}/work
    test ! -d ${catalina.base}/temp && mkdir ${catalina.base}/temp

    echo "prepare permfix"
    # because of sudo configuration the script must run from the "old ant" target dir, with the "old ant params" ...
    OLD_SCRIPT_DIR=$WORK_DIR/deployment/$TARGET_SUFFIX/target/scripts
    mkdir -p $OLD_SCRIPT_DIR
    cp $WORK_DIR/deployment/target/tomcat-$TARGET_SUFFIX-${use.config}/scripts/permfix.sh $OLD_SCRIPT_DIR/tomcat-permfix.sh
    echo "start permfix with: sudo sh $OLD_SCRIPT_DIR/tomcat-permfix.sh -instance ${webapp.service.name} -group tomcat"
    sudo sh $OLD_SCRIPT_DIR/tomcat-permfix.sh -instance ${webapp.service.name} -group tomcat

    echo "install initscript"
    /bin/sh $WORK_DIR/deployment/target/tomcat-$TARGET_SUFFIX-${use.config}/scripts/install-initscript.sh

    # kein start, damit kann vor dem start noch was ausgefuehrt werden, z.B. dbmaintain tasks
}


echo "$0 $1"
case $1 in
  "start" )
    start-tomcat-initscript ;;
  "stop" )
    stop-tomcat-initscript ;;
  "status" )
    status-tomcat-initscript ;;
  "deploy" )
    deploy ;;
  "deployAndStart" )
    deploy
    start-tomcat-initscript
    ;;
  * )
    echo "Usage $0 <start|stop|status|deploy|deployAndStart>"
    exit
esac
