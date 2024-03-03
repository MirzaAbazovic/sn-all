--
-- Legt die notwendigen Daten in der Authentication-DB an.
--

insert into APPLICATION (ID, NAME, DESCRIPTION) values (4, 'hurrican.web.services', 'WebServices von Hurrican');

insert into ACCOUNT (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD,
	MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER, FALLBACK_PASSWORD)
  values (27, 3, 4, 'hurrican.ws.billing.default', 'hurrican', 'T+95oGvGFTzWJVt4V+Bw1g==',
    10, 5, null, null, null);

insert into ACCOUNT (ID, DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD,
	MAX_ACTIVE, MAX_IDLE, DESCRIPTION, FALLBACK_USER, FALLBACK_PASSWORD)
  values (28, 4, 4, 'hurrican.ws.cc.writer', 'HURRICANWRITER', 'dyMRupr3P1Nl1OkoxnPWiQ==',
    20, 10, null, null, null);
    
alter session set nls_date_format='yyyy-mm-dd';
-- App-User anlegen (LDAP Benutzer)
insert into USERS (ID, LOGINNAME, NAME, FIRSTNAME, DEP_ID,
	EMAIL, ACTIVE, PHONE, FAX, PHONE_NEUTRAL, LDAP_OBJECTSID)
  values (450, 'hurrican-ws', 'ITS', 'WebServices', 10,
    null, 1, null, null, null, null);
    
-- Datenbanken bzw. Accounts dem User zuordnen
insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID) values (2000, 450, 27, 3);
insert into USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID) values (2001, 450, 28, 4);

-- App-Role anlegen und User zuordnen
insert into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT)
  values (200, 'AKHURRICAN_WS', 'Rolle fuer den Web-Service User', 4, 0, null);
insert into USERROLE (ID, USER_ID, ROLE_ID) values (2000, 450, 200);
  
commit;

-- Sequences erhoehen
drop sequence S_USERS_0;
create sequence S_USERS_0 start with 451;
grant select on S_USERS_0 to public;
commit;

drop sequence S_USERACCOUNT_0;
create sequence S_USERACCOUNT_0 start with 2002;
grant select on S_USERACCOUNT_0 to public;
commit;

drop sequence S_USERROLE_0;
create sequence S_USERROLE_0 start with 2001;
grant select on S_USERROLE_0 to public;
commit;

drop sequence S_ROLE_0;
create sequence S_ROLE_0 start with 201;
grant select on S_ROLE_0 to public;
commit;




