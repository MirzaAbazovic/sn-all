-- neuer Eintrag, damit hurrican-web auf die IP-Datenbank zugreifen darf

Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER,
    ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER,
    FALLBACK_PASSWORD)
 Values
   ((select max(id)+1 from ACCOUNT), (select id from db where name='ip'), (select id from application where name='hurrican.web'),
   'hurrican.web.ip.reader', 'scvlesen', '4Xm/NabFzbs=', NULL, NULL, NULL, NULL, NULL);

Insert into USERACCOUNT
  (ID, USER_ID, ACCOUNT_ID, DB_ID)
 Values
   ((select max(id)+1 from USERACCOUNT), (select id from users where loginname = 'hurrican-ws'),
   (select id from account where acc_name = 'hurrican.web.ip.reader'),
   (select id from db where name='ip'));

