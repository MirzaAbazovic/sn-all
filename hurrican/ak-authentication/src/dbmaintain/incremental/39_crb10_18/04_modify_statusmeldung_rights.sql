INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'taifun.status.feed.back'
            AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='verlauf.am.superuser');

        