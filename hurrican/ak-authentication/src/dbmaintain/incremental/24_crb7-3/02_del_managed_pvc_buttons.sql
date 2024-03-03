delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel'
        or PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel') and NAME = 'pvc.erfassen');
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel'
    or PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel') and NAME = 'pvc.erfassen';
