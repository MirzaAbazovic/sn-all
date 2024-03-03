delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.StrasseSearchDialog'));
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.StrasseSearchDialog');


delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (NAME = 'admin.strasse.action'));
delete from GUICOMPONENT where (NAME = 'admin.strasse.action');

