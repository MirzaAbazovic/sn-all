delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.RaBvPanel'));
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.RaBvPanel');

delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.RaBvDialog'));
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.shared.RaBvDialog');


