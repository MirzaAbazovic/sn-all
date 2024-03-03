delete from COMPBEHAVIOR where COMP_ID in
  (select gc.id from GUICOMPONENT gc where gc.parent='de.augustakom.hurrican.gui.tools.archivierung.SvRecherchePanel');

delete from GUICOMPONENT where parent='de.augustakom.hurrican.gui.tools.archivierung.SvRecherchePanel';

