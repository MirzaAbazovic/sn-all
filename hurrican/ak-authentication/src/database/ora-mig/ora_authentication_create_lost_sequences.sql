--
-- SQL-Script, um von DML nicht beruecksichtigte Sequences anzulegen.
--

drop sequence S_USERROLE_0;
select max(ID)+1 from USERROLE;
create sequence S_USERROLE_0 start with ?;
grant select on S_USERROLE_0 to public;


drop sequence S_USERACCOUNT_0;
select max(ID)+1 from USERACCOUNT;
create sequence S_USERACCOUNT_0 start with ?;
grant select on S_USERACCOUNT_0 to public;

drop sequence S_PWHISTORY_0;
select max(ID)+1 from PWHISTORY;
create sequence S_PWHISTORY_0 start with ?;
grant select on S_PWHISTORY_0 to public;

drop sequence S_DB_0;
select max(ID)+1 from DB;
create sequence S_DB_0 start with ?;
grant select on S_DB_0 to public;

drop sequence S_ACCOUNT_0;
select max(ID)+1 from ACCOUNT;
create sequence S_ACCOUNT_0 start with ?;
grant select on S_ACCOUNT_0 to public;

drop sequence S_COMPBEHAVIOR_0;
select max(ID)+1 from COMPBEHAVIOR;
create sequence S_COMPBEHAVIOR_0 start with ?;
grant select on S_COMPBEHAVIOR_0 to public;

commit;

 

 