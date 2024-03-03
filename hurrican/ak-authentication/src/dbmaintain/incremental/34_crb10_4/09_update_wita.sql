update GUICOMPONENT set PARENT='de.augustakom.hurrican.gui.tools.tal.wita.StornoDialog' where PARENT='de.augustakom.hurrican.gui.auftrag.StornoDialog';
update GUICOMPONENT set PARENT='de.augustakom.hurrican.gui.tools.tal.wita.TerminverschiebungDialog' where PARENT='de.augustakom.hurrican.gui.auftrag.TerminverschiebungDialog';
update GUICOMPONENT set PARENT='de.augustakom.hurrican.gui.tools.tal.wita.VorabstimmungDialog' where PARENT='de.augustakom.hurrican.gui.auftrag.VorabstimmungDialog';
update GUICOMPONENT set PARENT='de.augustakom.hurrican.gui.tools.tal.wita.VorabstimmungPanel' where PARENT='de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel';



Insert into GUICOMPONENT (ID, NAME, PARENT, APP_ID, VERSION)
 Values (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.tools.tal.wita.ErlmkDialog', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.tools.tal.wita.ErlmkDialog';


