
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'esaa.intern.history', 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'esaa.intern.history' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 8, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'esaa.intern.history' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 9, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'esaa.intern.history' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 25, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'esaa.intern.history' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 34, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'esaa.intern.history' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 236, '1', '1',
    0);

