
Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.TerminverschiebungDialog', 1, 0);

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'cb.vorgang.terminverschiebung', 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel', 1, 0);


Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'save' and parent = 'de.augustakom.hurrican.gui.auftrag.TerminverschiebungDialog'), 9, '1', '1',
    0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'cb.vorgang.terminverschiebung' and parent = 'de.augustakom.hurrican.gui.auftrag.CarrierbestellungPanel'), 9, '1', '1',
    0);



