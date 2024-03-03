alter table T_IA_LEVEL1 drop column bereich_id;
alter table T_IA_LEVEL1 add bereich_name varchar2(32);
alter table T_IA_LEVEL1 add constraint UQ_IA_LEVEL1_BEREICH unique(bereich_name);
