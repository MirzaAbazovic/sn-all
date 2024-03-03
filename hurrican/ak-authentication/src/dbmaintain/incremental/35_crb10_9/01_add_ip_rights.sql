-- IP Superuser zum administrieren der IP Konfiguration auf den Produkten
INSERT into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
  VALUES (S_ROLE_0.nextVal, 'admin.produkt.ip', 'Rollenrechte IP Konfiguration auf den Produkten', 1, '0', 0);

-- Administration/Produkt... für 'admin.produkt.ip' freigben
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'admin.produkte.action'
            AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');

-- Administration/Produkt.../Speichern Button für 'admin.produkt.ip' freigben
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'save'
            AND parent = 'de.augustakom.hurrican.gui.stammdaten.ProduktAdminFrame'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='admin.produkt.ip');



