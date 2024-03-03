INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
  SELECT S_USERROLE_0.nextVal, USER_ID_TMP, ROLE_ID_TMP
    FROM
      (select ID as USER_ID_TMP from USERS WHERE lower(LOGINNAME) in (
        'gamboeckma', 'tichy', 'schrodtdo',
        'symader', 'walinski', 'planskyhe',
        'huebscher', 'baumannma', 'petroviclo'
      )) ut,
      (select ID as ROLE_ID_TMP from ROLE WHERE name in ('admin.hvt', 'default.reader')) rt
  where not exists (select 1 from USERROLE urr where urr.USER_ID = ut.USER_ID_TMP and urr.ROLE_ID = rt.ROLE_ID_TMP)
;

