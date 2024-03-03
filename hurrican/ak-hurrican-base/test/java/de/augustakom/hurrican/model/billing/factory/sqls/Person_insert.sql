INSERT INTO PERSON (PERSON_NO, GENDER, NAME, FIRSTNAME, LANGUAGE, USERW, DATEW)
VALUES (
  ${personNo:-NULL},
  '${geschlecht:-NULL}',
  '${name:-NULL}',
  '${vorname:-NULL}',
  '${language:-NULL}',
  'TEST',
  sysdate
)