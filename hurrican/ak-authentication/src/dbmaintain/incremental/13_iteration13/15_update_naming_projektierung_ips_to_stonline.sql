-- Projektierung: IPS -> ST Online
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStOnlinePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungIPSPanel';

UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)IPS(.*)', '\1ST Online\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStOnlinePanel';

UPDATE GUICOMPONENT SET NAME = 'open.projektierung.stonline.action' WHERE NAME = 'open.projektierung.ips.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)IPS(.*)', '\1ST Online\2')
    WHERE NAME IN ('open.projektierung.ips.action');
