-- Bauauftrag: SCV -> AM (2.)
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE NAME IN ('verlauf.am', 'verlauf.am.superuser', 'am.default');

UPDATE GUICOMPONENT SET NAME = 'open.am.auftraege.action' WHERE NAME = 'open.scv.auftraege.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV.{4}(.*)', '\1AM\2')
    WHERE NAME = 'open.am.auftraege.action';

UPDATE GUICOMPONENT SET NAME = 'open.ba.verlauf.am.rl.action' WHERE NAME = 'open.ba.verlauf.scv.rl.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV.{4}(.*)', '\1AM\2')
    WHERE NAME = 'open.ba.verlauf.am.rl.action';
