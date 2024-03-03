-- Neue Rolle (wbci.superuser)
INSERT into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
  VALUES (S_ROLE_0.nextVal, 'wbci.superuser', 'Rolle fuer WBCI SuperUser.', 1, '0', 0);

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
  SELECT S_USERROLE_0.nextVal, USER_ID_TMP, ROLE_ID_TMP
    FROM
      (select ID as USER_ID_TMP from USERS WHERE lower(LOGINNAME) in (
        'seyfarthno',
        'sunkeyv',
        'glinkjo',
        'schneckto',
        'todorovge',
        'deppischch',
        'maherma'
      )),
      (select ID as ROLE_ID_TMP from ROLE WHERE name='wbci.superuser');