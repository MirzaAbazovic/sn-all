#!/bin/bash
#
# $Id$
#
#

die () {
    echo `basename $0`': '$*
    exit 1
}

cd `dirname $0`

sudo install -o root -g root -m 0755 initscript /etc/init.d/${webapp.service.name} || die install failed

sudo /sbin/chkconfig ${webapp.service.name} on || die chkconfig failed
sudo /sbin/chkconfig -l ${webapp.service.name}

