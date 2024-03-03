Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'taifun.status.feed.back', 'de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel',
    'CheckBox', 'CheckBox to activate/deactivate order status feed back to taifun', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'taifun.status.feed.back'
            AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='HURRICAN_AM_SUPERUSER');
