-- Projektierung: EWSD -> ST Voice
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSdhEwsdPanel.SDH';
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSdhEwsdPanel';

UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH und EWSD(.*)', '\1ST Connect\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel';

UPDATE GUICOMPONENT SET NAME = 'open.projektierung.stconnect.action' WHERE NAME = 'open.projektierung.sdh.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH(.*)', '\1ST Connect\2')
    WHERE NAME IN ('open.projektierung.stconnect.action');
