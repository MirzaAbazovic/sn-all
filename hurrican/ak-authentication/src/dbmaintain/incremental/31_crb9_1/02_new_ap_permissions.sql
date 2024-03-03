
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'address.copy', 'de.augustakom.hurrican.gui.shared.CCAddressPanel', 1, 0);
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'copy.ap.ga', 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel', 1, 0);
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'copy.ap.hotlineservice', 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel', 1, 0);
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'copy.ap.action', 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel', 1, 0);


Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'address.copy'), 336, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'address.copy'), 9, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'address.copy'), 45, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'address.copy'), 30, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.ga'), 336, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.ga'), 9, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.ga'), 45, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.ga'), 30, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.hotlineservice'), 336, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.hotlineservice'), 9, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.hotlineservice'), 45, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.hotlineservice'), 30, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.action'), 336, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.action'), 9, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.action'), 45, '1', '1',
    0);
Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'copy.ap.action'), 30, '1', '1',
    0);


