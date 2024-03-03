DELETE FROM COMPBEHAVIOR
      WHERE Role_ID = (SELECT ID
                         FROM Role
                        WHERE name = 'wita.clearing');

DELETE FROM USERROLE
      WHERE USER_ID IN
               (SELECT ID AS USER_ID_TMP
                  FROM USERS
                 WHERE LOGINNAME IN
                          ('HimmelCh', 'schmittse', 'SEYFARTHNO', 'UysalHi'))
            AND role_id = (SELECT ID AS ROLE_ID_TMP
                             FROM ROLE
                            WHERE name = 'wita.clearing');

DELETE FROM Role
      WHERE name = 'wita.clearing';


INSERT INTO ROLE
     VALUES (S_ROLE_0.NEXTVAL,
             'wita.clearing',
             'Rolle fur WITA Clearing Team',
             1,
             0,
             NULL,
             0);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
   SELECT S_USERROLE_0.NEXTVAL, USER_ID_TMP, ROLE_ID_TMP
     FROM (SELECT ID AS USER_ID_TMP
             FROM USERS
            WHERE LOGINNAME IN
                     ('HimmelCh', 'schmittse', 'SEYFARTHNO', 'UysalHi')),
          (SELECT ID AS ROLE_ID_TMP
             FROM ROLE
            WHERE name = 'wita.clearing');

INSERT INTO COMPBEHAVIOR (ID,
                          ROLE_ID,
                          VISIBLE,
                          EXECUTABLE,
                          COMP_ID)
     VALUES (
               S_COMPBEHAVIOR_0.NEXTVAL,
               (SELECT ID
                  FROM ROLE
                 WHERE name = 'wita.clearing'),
               '1',
               '1',
               (SELECT ID
                  FROM GUICOMPONENT
                 WHERE name = 'WitaKlaerfaellePanel'
                       AND parent =
                              'de.augustakom.hurrican.gui.tools.tal.UnfinishedCarrierBestellungFrame'));
