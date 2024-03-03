-- Verwendungszweck(Combobox) anlegen
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'cb.purpose', 'de.augustakom.hurrican.gui.auftrag.internet.AssignIPAddressDialog',
    'COMBOBOX', 'Verwendungszweck im IP Zuweisungsdialog', 1, 0);

-- Verwendungszweck(Combobox) verrechten
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'cb.purpose'
            AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.AssignIPAddressDialog'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='assign.ip.ripe');
