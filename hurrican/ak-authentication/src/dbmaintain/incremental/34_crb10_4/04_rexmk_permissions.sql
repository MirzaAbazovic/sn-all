Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'create.rexmk.cbv', 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'create.rexmk.cbv' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 9, '1', '1',
    0);



