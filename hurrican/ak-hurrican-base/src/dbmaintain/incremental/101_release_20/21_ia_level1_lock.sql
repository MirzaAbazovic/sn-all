alter table t_ia_level1 add lock_mode char(1) DEFAULT '0' not null;
alter table t_ia_level1 add constraint CK_IA_LEVEL_1_LOCK_MODE check (lock_mode in ('0', '1'));

update t_ia_level1 set lock_mode = '1' where bereich_name = 'TE-PD';
