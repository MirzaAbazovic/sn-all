-- RIPE IP Role
INSERT into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
  VALUES (S_ROLE_0.nextVal, 'assign.ip.ripe', 'Rollenrecht um den Verwendungszweck(Purpose) für IP V4 Zuweisungen zu aendern', 1, '0', 0);

-- RIPE IP Recht vergeben
INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
  SELECT S_USERROLE_0.nextVal, USER_ID_TMP, ROLE_ID_TMP
    FROM
      (select ID as USER_ID_TMP from USERS WHERE lower(LOGINNAME) in (
        'guggra',
        'scharrch',
        'babicce',
        'figulafl',
        'markovicth',
        'conradlo',
        'jansenal',
        'endres',
        'markovicpr',
        'hessre',
        'nutzle',
        'petermann',
        'kirchner',
        'markart',
        'merettafe',
        'muenstermannjo',
        'rauwolfxa',
        'garbe',
        'buchbergerth',
        'titzjo'
      )),
      (select ID as ROLE_ID_TMP from ROLE WHERE name='assign.ip.ripe');

