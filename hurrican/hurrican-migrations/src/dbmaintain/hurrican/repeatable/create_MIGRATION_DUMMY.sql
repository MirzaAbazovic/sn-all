create or replace force VIEW MIGRATION_DUMMY
 as select 1 as ID from dual;
 
grant select on MIGRATION_DUMMY to R_HURRICAN_USER;
