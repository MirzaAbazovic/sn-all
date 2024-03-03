-- Bauauftrag: SCT -> FieldService
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragSCTPanel';
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragSCTPanel.SCT';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN
    (SELECT ID FROM GUICOMPONENT WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragSCTPanel.SCTK');
DELETE FROM GUICOMPONENT WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragSCTPanel.SCTK';
UPDATE GUICOMPONENT SET NAME = 'ba.erledigen' WHERE NAME = 'ba.erledigen.sct';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT(.*)', '\1FieldService\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT und SCTK(.*)', '\1FieldService\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel';

UPDATE ROLE SET NAME = 'verlauf.fieldservice' WHERE NAME = 'verlauf.sct';
UPDATE ROLE SET NAME = 'verlauf.fieldservice.superuser' WHERE NAME = 'verlauf.sct.superuser';
UPDATE ROLE SET NAME = 'HURRICAN_FIELDSERVICE_DEFAULT' WHERE NAME = 'HURRICAN_SCT_DEFAULT';
UPDATE ROLE SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT(.*)', '\1FieldService\2')
    WHERE NAME = 'verlauf.fieldservice';

UPDATE GUICOMPONENT SET NAME = 'open.ba.verlauf.fieldservice.action' WHERE NAME = 'open.ba.verlauf.sct.action';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT WHERE NAME = 'open.ba.verlauf.sctk.action');
DELETE FROM GUICOMPONENT WHERE NAME = 'open.ba.verlauf.sctk.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT.{4}(.*)', '\1FieldService\2')
    WHERE NAME = 'open.ba.verlauf.fieldservice.action';

UPDATE GUICOMPONENT SET NAME = 'fieldservice.sperren.action' WHERE NAME = 'sct.sperren.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT(.*)', '\1FieldService\2')
    WHERE NAME = 'fieldservice.sperren.action';


