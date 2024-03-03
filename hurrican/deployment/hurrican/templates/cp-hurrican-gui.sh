#!/bin/sh

set -e

SRCDIR="$(dirname $0)"
DESTDIR=${hurrican.gui.installpath}
CURRENT_TIME=$(date +"%Y-%m-%d-%H%M%S")

GUI=ak-hurrican-gui
SRCDIR_GUI="$SRCDIR/$GUI"
DESTDIR_GUI="$DESTDIR/$GUI-$CURRENT_TIME"

if [ -d "$DESTDIR_GUI" -a "$DESTDIR_GUI" != "/" ]; then
    echo "Removing $DESTDIR_GUI"
    rm -rf "$DESTDIR_GUI"
fi

echo "Copying $SRCDIR_GUI to $DESTDIR_GUI"
cp -r "$SRCDIR_GUI" "$DESTDIR_GUI"
