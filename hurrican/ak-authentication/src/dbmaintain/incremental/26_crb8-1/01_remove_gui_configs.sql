delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.auftrag.ExportCommandDialog'));
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.auftrag.ExportCommandDialog');
