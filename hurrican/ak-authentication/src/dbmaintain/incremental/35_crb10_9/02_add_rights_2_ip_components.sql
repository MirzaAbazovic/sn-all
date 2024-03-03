-- IP Pool
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'ip.pool', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'COMBOBOX', 'Konfiguration der IP Pools', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'ip.pool'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Verwendungszweck für V4 Adressen (Purpose)
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'verwendungszweck.v4', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'COMBOBOX', 'Konfiguration des IP V4 Verwendungszweckes', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'verwendungszweck.v4'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Verwendungszweck V4 editierbar
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'verwendungszweck.v4.editierbar', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'CHECKBOX', 'Ist der V4 Verwendungszwecke editierbar?', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'verwendungszweck.v4.editierbar'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Netzmaske V4
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'netzmaske.v4', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'TEXTFIELD', 'Groesse der V4 Netzmaske', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'netzmaske.v4'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Netzmaske V6
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'netzmaske.v6', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'TEXTFIELD', 'Groesse der V6 Netzmaske', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'netzmaske.v6'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Netzmaske editierbar
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'netzmaske.editierbar', 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel',
    'CHECKBOX', 'Ist die Netzmaske editierbar?', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'netzmaske.editierbar'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.Produkt2AdminPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');
