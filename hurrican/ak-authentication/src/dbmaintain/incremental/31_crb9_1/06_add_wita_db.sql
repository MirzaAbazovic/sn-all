-- WITA DB in Authentication eintragen


Insert into DB
   (ID, NAME, DRIVER, URL, SCHEMA, HIBERNATE_DIALECT, DESCRIPTION, VERSION)
 Values
   (5, 'wita', 'oracle.jdbc.driver.OracleDriver', 'jdbc:oracle:thin:@mnetdbsvr15.m-net.de:1521:HCDEV01', 'HURRICAN_CI',
    'org.hibernate.dialect.Oracle10gDialect', 'Definition fuer die Wita-DB.', 0);


Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, VERSION)
 Values
   (S_ACCOUNT_0.nextVal, 5, 4, 'hurrican.web.wita.writer', 'HURRICANWRITER',
    'dyMRupr3P1Nl1OkoxnPWiQ==', 30, 3, 0);

-- neuen DB-Account dem User 'hurrican-ws' zuordnen
Insert into USERACCOUNT
   (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION)
 Values
   (S_USERACCOUNT_0.nextVal, 1220, (select acc.id from ACCOUNT acc where acc.ACC_NAME='hurrican.web.wita.writer'), 5, 0);
-- neuen DB-Account dem User 'UnitTest' zuordnen
Insert into USERACCOUNT
   (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION)
 Values
   (S_USERACCOUNT_0.nextVal, 151, (select acc.id from ACCOUNT acc where acc.ACC_NAME='hurrican.web.wita.writer'), 5, 0);


