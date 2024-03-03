-- Projektierung: SCT -> FieldService
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSctPanel';
UPDATE GUICOMPONENT SET PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel'
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSctPanel.SCT';

DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN
    (SELECT ID FROM GUICOMPONENT WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSctPanel.SCTK');
DELETE FROM GUICOMPONENT WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungSctPanel.SCTK';

UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT, SCTK[[:cntrl:][:space:]]+und NP(.*)', '\1FieldService\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT(.*)', '\1FieldService\2')
    WHERE PARENT = 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel';

UPDATE GUICOMPONENT SET NAME = 'open.projektierung.fieldservice.action' WHERE NAME = 'open.projektierung.sct.action';
DELETE FROM COMPBEHAVIOR WHERE COMP_ID IN (SELECT ID FROM GUICOMPONENT WHERE NAME = 'open.projektierung.sctk.action');
DELETE FROM GUICOMPONENT WHERE NAME = 'open.projektierung.sctk.action';
UPDATE GUICOMPONENT SET DESCRIPTION = REGEXP_REPLACE(DESCRIPTION, '(.*)SCT(.*)', '\1FieldService\2')
    WHERE NAME = 'open.projektierung.fieldservice.action';
