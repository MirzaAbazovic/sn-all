-- Bauauftrag: SCV -> AM
UPDATE GUICOMPONENT SET NAME = 'ba.abschliessen' WHERE NAME = 'ba.abschliessen.am';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragAmRlPanel';

UPDATE ROLE SET NAME = 'verlauf.am' WHERE NAME = 'verlauf.scv';
UPDATE ROLE SET NAME = 'verlauf.am.superuser' WHERE NAME = 'scv.super.user';
UPDATE ROLE SET NAME = 'am.default' WHERE NAME = 'scv.default';
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE NAME IN ('verlauf.am', 'verlauf.am.superuser', 'am.default''verlauf.fieldservice');
