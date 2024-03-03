-- Bauauftrag: SCV -> AM (descriptions)
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCV(.*)', '\1AM\2')
    WHERE NAME IN ('open.ba.verlauf.am.rl.action', 'open.ba.verlauf.am.rl.action', 'am.db.checks.action');


