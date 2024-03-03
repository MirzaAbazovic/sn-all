-- Projektierung: EWSD -> ST Voice
INSERT INTO GUICOMPONENT (SELECT S_GUICOMPONENT_0.NEXTVAL, NAME,
            'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel', TYPE, DESCRIPTION, APP_ID, 0
    FROM GUICOMPONENT WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSdhEwsdPanel');
INSERT INTO COMPBEHAVIOR (SELECT S_COMPBEHAVIOR_0.NEXTVAL, g.ID, r.ID, '1', '1', 0
    FROM GUICOMPONENT g, ROLE r
        WHERE r.NAME = 'verlauf.view' AND G.PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel');

UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSdhEwsdPanel.EWSD';

UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SDH und EWSD(.*)', '\1ST Voice\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)EWSD(.*)', '\1ST Voice\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel';

UPDATE GUICOMPONENT SET NAME = 'open.projektierung.stvoice.action' WHERE NAME = 'open.projektierung.ewsd.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)EWSD(.*)', '\1ST Voice\2')
    WHERE NAME IN ('open.projektierung.stvoice.action', 'open.ba.verlauf.stvoice.action');
