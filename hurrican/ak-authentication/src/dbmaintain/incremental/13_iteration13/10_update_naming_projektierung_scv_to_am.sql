-- Projektierung: SCV -> AM
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungAmRlPanel';

UPDATE GUICOMPONENT SET NAME = 'open.projektierung.am.rl.action' WHERE NAME = 'open.projektierung.scv.rl.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE NAME = 'open.projektierung.am.rl.action';
