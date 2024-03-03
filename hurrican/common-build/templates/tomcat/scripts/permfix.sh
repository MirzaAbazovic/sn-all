#!/bin/sh
#
# maven variante ...

die () {
    echo `basename $0`': '$*
    exit 1
}

grep ^${tomcat.user} /etc/passwd >/dev/null || die "User '${tomcat.user}' is missing."
grep ^${tomcat.group} /etc/group >/dev/null || die "User group '${tomcat.group}' is missing."

echo "default perms setzen"
find ${webapp.install.path} -type d -exec chmod 2775 {} \;
find ${webapp.install.path} -type f -exec chmod 0664 {} \;

echo "exec perm fuer java"
test -d ${webapp.install.path}/jre*/bin && chmod 0755 ${webapp.install.path}/jre*/bin/*
test -d ${webapp.install.path}/jdk*/bin && chmod 0755 ${webapp.install.path}/jdk*/bin/*

echo "exec perm fuer scripte"
chmod 2755 ${catalina.base}/bin/*.sh || die 48
chmod 0755 ${catalina.home}/bin/*.sh || die 49

for DIR in ${webapp.install.path}/* ; do
    echo "user:group '${tomcat.user}:${tomcat.group}' fuer '$DIR'"
    chown -R ${tomcat.user}:${tomcat.group} $DIR || die 45
done