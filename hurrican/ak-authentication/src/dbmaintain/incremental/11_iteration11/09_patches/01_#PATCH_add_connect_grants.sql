Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'zpconnect'), (select id from role where name = 'admin.hvt'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'walinski'), (select id from role where name = 'admin.hvt'), 0);

Insert into USERROLE (ID, USER_ID, ROLE_ID, VERSION)
 Values (S_USERROLE_0.nextval, (select id from users where lower(loginname) = 'pacher'), (select id from role where name = 'admin.hvt'), 0);
