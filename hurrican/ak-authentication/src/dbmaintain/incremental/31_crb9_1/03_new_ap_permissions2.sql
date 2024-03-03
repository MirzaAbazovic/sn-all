Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.CCAnsprechpartnerTypDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.CCAnsprechpartnerTypDialog'), 336, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.CCAnsprechpartnerTypDialog'), 9, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.CCAnsprechpartnerTypDialog'), 45, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.CCAnsprechpartnerTypDialog'), 30, '1', '1',
    0);