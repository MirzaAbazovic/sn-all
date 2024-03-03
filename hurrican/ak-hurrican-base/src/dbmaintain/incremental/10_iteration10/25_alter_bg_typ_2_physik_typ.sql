delete from T_HW_BG_TYP_2_PHYSIK_TYP where BAUGRUPPEN_TYP_ID = (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITF')
 or BAUGRUPPEN_TYP_ID = (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITFG');

insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITF'), (select ID from T_PHYSIKTYP where NAME = 'AB'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITF'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-ab (Alc)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITFG'), (select ID from T_PHYSIKTYP where NAME = 'AB'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITFG'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-ab (Alc)'));

ALTER TABLE T_HW_BG_TYP_2_PHYSIK_TYP ADD CONSTRAINT UK_T_HW_BG_TYP_2_PT UNIQUE (BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID);
