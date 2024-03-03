-- Bauauftrag: SDH -> ST Connect
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragSDHPanel';
UPDATE GUICOMPONENT SET NAME = 'ba.erledigen' WHERE NAME = 'ba.erledigen.sdh';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel';

UPDATE ROLE SET NAME = 'verlauf.stconnect' WHERE NAME = 'verlauf.sdh';
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE NAME = 'verlauf.stconnect';

UPDATE GUICOMPONENT SET NAME = 'open.ba.verlauf.stconnect.action' WHERE NAME = 'open.ba.verlauf.sdh.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE NAME = 'open.ba.verlauf.stconnect.action';

