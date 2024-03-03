alter table T_CB_VORGANG add (ABM_STATE VARCHAR2(10));

alter table T_CB_VORGANG add (
  constraint T_CB_VORGANG_ABM_STATE
  check (ABM_STATE in ('NO_ABM','FIRST_ABM','SECOND_ABM')));

update T_CB_VORGANG set ABM_STATE = 'NO_ABM' where CBV_TYPE = 'WITA';
update T_CB_VORGANG set ABM_STATE = 'SECOND_ABM' where SECOND_ABM_RECEIVED = 1;

alter table T_CB_VORGANG drop column SECOND_ABM_RECEIVED;
