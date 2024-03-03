
alter table T_ANSPRECHPARTNER add PRIO NUMBER(10);
update T_ANSPRECHPARTNER set PRIO = 1 where PREFERRED = 1;
update T_ANSPRECHPARTNER set PRIO = 2 where PREFERRED = 0;

alter table T_ADDRESS add PRIO_BRIEF NUMBER(10);
update T_ADDRESS set PRIO_BRIEF = 1;
alter table T_ADDRESS add PRIO_EMAIL NUMBER(10);
update T_ADDRESS set PRIO_EMAIL = 1;
alter table T_ADDRESS add PRIO_FAX NUMBER(10);
update T_ADDRESS set PRIO_FAX = 1;
alter table T_ADDRESS add PRIO_SMS NUMBER(10);
update T_ADDRESS set PRIO_SMS = 1;
alter table T_ADDRESS add PRIO_TEL NUMBER(10);
update T_ADDRESS set PRIO_TEL = 1;




