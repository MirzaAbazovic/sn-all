--
-- Script, um die ID Columns auf NUMBER(10) zu aendern
--

alter table ACCOUNT modify ID NUMBER(10);
alter table ACCOUNT modify DB_ID NUMBER(10);
alter table ACCOUNT modify APP_ID NUMBER(10);

alter table APPLICATION modify ID NUMBER(10);

alter table COMPBEHAVIOR modify ROLE_ID NUMBER(10);
alter table GUICOMPONENT modify APP_ID NUMBER(10);

alter table DEPARTMENT modify ID NUMBER(10);

alter table PWHISTORY modify USER_ID NUMBER(10);

alter table ROLE modify ID NUMBER(10);
alter table ROLE modify APP_ID NUMBER(10);

alter table USERACCOUNT modify USER_ID NUMBER(10);
alter table USERACCOUNT modify ACCOUNT_ID NUMBER(10);
alter table USERACCOUNT modify DB_ID NUMBER(10);

alter table USERROLE modify USER_ID NUMBER(10);
alter table USERROLE modify ROLE_ID NUMBER(10);

alter table USERS modify ID NUMBER(10);
alter table USERS modify DEP_ID NUMBER(10);

alter table USERSESSION modify USER_ID NUMBER(10);
commit;


