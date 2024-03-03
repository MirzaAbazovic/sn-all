Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'WitaKlaerfaellePanel', 'de.augustakom.hurrican.gui.tools.tal.UnfinishedCarrierBestellungFrame', null, 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 1, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'WitaKlaerfaellePanel' AND parent = 'de.augustakom.hurrican.gui.tools.tal.UnfinishedCarrierBestellungFrame';
