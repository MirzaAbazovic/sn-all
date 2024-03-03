
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.produktGruppe', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.produktGruppe'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

    Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.providerVertragsnr', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.providerVertragsnr'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.providerLbz', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.providerLbz'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.bestandssucheOnkz', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.bestandssucheOnkz'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.bestandssucheDn', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.bestandssucheDn'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'vorabstimmung.bestandssucheDirectDial', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'vorabstimmung.bestandssucheDirectDial'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungPanel'),
   9, '1', '1', 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.VorabstimmungDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'save'
                                  and parent = 'de.augustakom.hurrican.gui.auftrag.VorabstimmungDialog'),
   9, '1', '1', 0);