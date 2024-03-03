Insert into USERS
   (ID, LOGINNAME, NAME, FIRSTNAME, DEP_ID, PASSWORD, PWDEPRECATION, EMAIL, ACTIVE, NIEDERLASSUNG_ID, PROJEKTLEITER, VERSION, MANAGER, PHONE)
 Values
   (0, 'WitaUnitTest', 'WitaUnitTest', 'M-net', 10,
    'cYpldbU0kYI=', TO_DATE('01/01/2030 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'joachim.glink@m-net.de', '1',
    1, '0', 0, '0', '0821/4500-3284');

insert into userrole (id, user_id, role_id) values (s_userrole_0.nextVal, 0, 200);
insert into userrole (id, user_id, role_id) values (s_userrole_0.nextVal, 0, 1);

Insert into ACCOUNT
   (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, MAX_ACTIVE, MAX_IDLE, VERSION)
 Values
   (65, 5, 1, 'hurrican.wita.writer', 'HURRICANWRITER',
    'dyMRupr3P1Nl1OkoxnPWiQ==', 30, 3, 0);


Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 2, 3, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 3, 4, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 22, 10, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 23, 11, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 25, 12, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 30, 13, 0);
Insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION) Values (s_useraccount_0.nextVal, 0, 65, 5, 0);
