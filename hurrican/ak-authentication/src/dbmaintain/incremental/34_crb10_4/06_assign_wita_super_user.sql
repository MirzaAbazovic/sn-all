INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
  SELECT S_USERROLE_0.nextVal, USER_ID_TMP, ROLE_ID_TMP
    FROM
      (select ID as USER_ID_TMP from USERS WHERE lower(LOGINNAME) in (
        'weissmi',
        'pfisterth',
        'albrechtst',
        'koenigul',
        'fischersi',
        'pacher',
        'tichy',
        'hieke',
        'tschernich',
        'bauerfl',
        'rueckertke',
        'freundka',
        'hauffean',
        'spanheimerso',
        'schmidev',
        'eichleiterge',
        'satkefr',
        'buergermeister',
        'himmelch',
        'roechnerma',
        'giesbergpe',
        'harderma',
        'schmittse',
        'oetterbe',
        'dippelch',
        'listlth',
        'hammerre'
      )),
      (select ID as ROLE_ID_TMP from ROLE WHERE name='wita.superuser');

