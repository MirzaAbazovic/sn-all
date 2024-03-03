#!/bin/sh

set -e
set -x

DESTDIR=${hurrican.gui.installpath}
HUR_MODULE=ak-hurrican-gui
DESTDIR_HUR_MODULE="$DESTDIR/*$HUR_MODULE-*"

echo "Removing old GUI files '$DESTDIR_HUR_MODULE'"
rm -rvf $DESTDIR_HUR_MODULE || echo "Could not remove old files. Maybe they are locked by a user."
