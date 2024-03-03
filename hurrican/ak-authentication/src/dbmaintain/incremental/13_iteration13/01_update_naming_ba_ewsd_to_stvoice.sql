-- Bauauftrag: EWSD -> ST Voice
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragEWSDPanel';
UPDATE GUICOMPONENT SET NAME = 'ba.erledigen.stvoice' WHERE NAME = 'ba.erledigen.ewsd';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)EWSD(.*)', '\1ST Voice\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel';

UPDATE ROLE SET NAME = 'verlauf.stvoice' WHERE NAME = 'verlauf.ewsd';
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)EWSD(.*)', '\1ST Voice\2')
    WHERE NAME = 'verlauf.stvoice';


UPDATE GUICOMPONENT SET NAME = 'open.ba.verlauf.stvoice.action' WHERE NAME = 'open.ba.verlauf.ewsd.action';


