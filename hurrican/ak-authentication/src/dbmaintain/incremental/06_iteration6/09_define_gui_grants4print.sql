
-- Rolle 'auftrag.druck' dem User 'stvoice'/'stonline' zuordnen
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 1420, 36 from dual);
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 1419, 36 from dual);


