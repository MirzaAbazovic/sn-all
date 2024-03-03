-- Bauauftrag: IPS -> ST Online
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragIPSPanel';
UPDATE GUICOMPONENT SET NAME = 'ba.erledigen' WHERE NAME = 'ba.erledigen.ips';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)IPS(.*)', '\1ST Online\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel';

UPDATE ROLE SET NAME = 'verlauf.stonline' WHERE NAME = 'verlauf.ips';
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)IPS(.*)', '\1ST Online\2')
    WHERE NAME = 'verlauf.stonline';

UPDATE GUICOMPONENT SET NAME = 'open.ba.verlauf.stonline.action' WHERE NAME = 'open.ba.verlauf.ips.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)IPS(.*)', '\1ST Online\2')
    WHERE NAME = 'open.ba.verlauf.stonline.action';

