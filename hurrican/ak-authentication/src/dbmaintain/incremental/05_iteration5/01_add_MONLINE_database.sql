--
-- Adds the connection to MONLINE to the authentication database
-- grants access for all users having had access to the ip database in agb
--

Insert into DB
   (ID, NAME, DRIVER, URL, "SCHEMA",
    HIBERNATE_DIALECT, DESCRIPTION, FALLBACK_DRIVER, FALLBACK_URL, FALLBACK_SCHEMA,
    FALLBACK_HIBERNATE_DIALECT)
 Values
   (13, 'monline', 'oracle.jdbc.driver.OracleDriver', 'jdbc:oracle:thin:@mnetdbsvr10:1521:KPMIG01', 'MONLINE',
    'net.sf.hibernate.dialect.Oracle9Dialect', 'Definition fuer die Monline-Datenbank', NULL, NULL, NULL,
    NULL);

Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER,
    ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER,
    FALLBACK_PASSWORD)
 Values
   (30, 13, 1, 'monline.default', 'HURRICAN',
    '7x9Z8IHQmPeJofJgQAHr0A==', 2, 0, NULL, NULL,
    NULL);
Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER,
    ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER,
    FALLBACK_PASSWORD)
 Values
   (31, 13, 4, 'monline.web', 'HURRICAN',
    '7x9Z8IHQmPeJofJgQAHr0A==', 2, 0, NULL, NULL,
    NULL);
Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER,
    ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER,
    FALLBACK_PASSWORD)
 Values
   (32, 13, 2, 'monline.scheduler', 'HURRICAN',
    '7x9Z8IHQmPeJofJgQAHr0A==', 2, 0, NULL, NULL,
    NULL);
Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER,
    ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER,
    FALLBACK_PASSWORD)
 Values
   (33, 13, 3, 'monline.reporting', 'HURRICAN',
    '7x9Z8IHQmPeJofJgQAHr0A==', 2, 0, NULL, NULL,
    NULL);

insert into useraccount
 (ID, USER_ID, ACCOUNT_ID, DB_ID)
select S_USERACCOUNT_0.nextval, user_id, 30, 13
from useraccount
where account_id = 6;

insert into useraccount
 (ID, USER_ID, ACCOUNT_ID, DB_ID)
select S_USERACCOUNT_0.nextval, user_id, 31, 13
from useraccount
where account_id = 29;

insert into useraccount
 (ID, USER_ID, ACCOUNT_ID, DB_ID)
select S_USERACCOUNT_0.nextval, user_id, 32, 13
from useraccount
where account_id = 10;

insert into useraccount
 (ID, USER_ID, ACCOUNT_ID, DB_ID)
select S_USERACCOUNT_0.nextval, user_id, 33, 13
from useraccount
where account_id = 18;

COMMIT;
