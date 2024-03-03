--
-- Script, um die ID Columns auf NUMBER(10) zu aendern
--

alter table DB modify ID NUMBER(10);
alter table USERSESSION modify APP_ID NUMBER(10);
commit;


