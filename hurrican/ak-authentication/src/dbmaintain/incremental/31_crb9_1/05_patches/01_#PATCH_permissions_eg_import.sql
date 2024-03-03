Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'import.eg', 'de.augustakom.hurrican.gui.auftrag.EG2AuftragPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'import.eg'),9, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'import.eg'), 26, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'import.eg'), 28, '1', '1',
    0);