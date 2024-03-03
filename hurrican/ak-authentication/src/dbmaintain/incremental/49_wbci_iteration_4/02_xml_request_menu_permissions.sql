-- GIU-komponente wurde verschoben und umbennant --

update GUICOMPONENT
  set parent = 'de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryDialog'
where parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog';
