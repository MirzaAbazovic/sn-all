--
-- SQL-Script, um Datentypen zu veraendern
--

--connect AUTHENTICATION@CCPROD;

alter table COMPBEHAVIOR modify ( VISIBLE CHAR(1) );
alter table COMPBEHAVIOR modify ( EXECUTABLE CHAR(1) );
alter table USERS modify ( ACTIVE CHAR(1) );
alter table ROLE modify ( IS_ADMIN CHAR(1) );

commit;


