--
-- SQL-Script, das auf der Source-DB ausgefuehrt werden muss,
-- bevor die DML-Tools gestartet werden
--

-- Datentypen aendern


-- Tabelle USER umbenennen
use authentication;
create table users as select * from user;


alter table USERACCOUNT DROP FOREIGN KEY FK_USERACCOUNT_TO_USER;
alter table USERROLE DROP FOREIGN KEY FK_UR_TO_USER;
alter table USERSESSION DROP FOREIGN KEY FK_USERSESSION_TO_USER;
alter table PWHISTORY DROP FOREIGN KEY FK_PWHISTORY_TO_USER;

drop table user;


   
   