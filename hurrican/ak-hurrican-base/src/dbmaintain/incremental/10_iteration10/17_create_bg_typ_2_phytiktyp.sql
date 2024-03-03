
CREATE TABLE T_HW_BG_TYP_2_PHYSIK_TYP
(
  ID                 NUMBER(10)                 NOT NULL,
  BAUGRUPPEN_TYP_ID  NUMBER(10)                 NOT NULL,
  PHYSIKTYP_ID       NUMBER(10)                 NOT NULL
) LOGGING NOCOMPRESS NOCACHE NOPARALLEL NOMONITORING;

COMMENT ON TABLE T_HW_BG_TYP_2_PHYSIK_TYP IS 'Mappt, welche Baugruppen für welchen Physiktypp genutzt werden koennen';


ALTER TABLE T_HW_BG_TYP_2_PHYSIK_TYP ADD (CONSTRAINT PK_T_HW_BG_TYP_2_PHYSIK_TYP PRIMARY KEY (ID));

ALTER TABLE T_HW_BG_TYP_2_PHYSIK_TYP ADD (
  CONSTRAINT FK_BG_TYP_2_PT_BG_TYP FOREIGN KEY (BAUGRUPPEN_TYP_ID) REFERENCES T_HW_BAUGRUPPEN_TYP (ID)
    DEFERRABLE INITIALLY IMMEDIATE,
  CONSTRAINT FK_BG_TYP_2_PT_PT FOREIGN KEY (PHYSIKTYP_ID) REFERENCES T_PHYSIKTYP (ID)
    DEFERRABLE INITIALLY IMMEDIATE);

CREATE SEQUENCE S_T_HW_BG_TYP_2_PHYSIK_TYP_0
START WITH 1 INCREMENT BY 1 MINVALUE 1 NOCACHE NOCYCLE NOORDER;

GRANT select on T_HW_BG_TYP_2_PHYSIK_TYP to HURRICANREADER;
GRANT select, insert, update, delete on T_HW_BG_TYP_2_PHYSIK_TYP to HURRICANWRITER;

GRANT select on S_T_HW_BG_TYP_2_PHYSIK_TYP_0 to PUBLIC;

insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'ADLTK'), (select ID from T_PHYSIKTYP where NAME = 'ADSL (Alc-ATM)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'ADLTN'), (select ID from T_PHYSIKTYP where NAME = 'ADSL (Alc-ATM)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'ABLTF'), (select ID from T_PHYSIKTYP where NAME = 'ADSL (Alc-ATM)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'ABLTF'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ (Alc-IP)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'ABLTF'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ only (Alc-IP)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'EBLTD'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ (Alc-IP)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'EBLTD'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ only (Alc-IP)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'NALTD'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ (Alc-IP)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'NALTD'), (select ID from T_PHYSIKTYP where NAME = 'ADSL2+ only (Alc-IP)'));

insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITF'), (select ID from T_PHYSIKTYP where NAME = 'AB'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITF'), (select ID from T_PHYSIKTYP where NAME = 'AB'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITFG'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-ab (Alc)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMAITFG'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-ab (Alc)'));

insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMDTFB'), (select ID from T_PHYSIKTYP where NAME = 'UK0'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMDTFB'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-UK0 (Alc)'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMDTFC'), (select ID from T_PHYSIKTYP where NAME = 'UK0'));
insert into T_HW_BG_TYP_2_PHYSIK_TYP (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID) values (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval,
 (select ID from T_HW_BAUGRUPPEN_TYP where name = 'SLMDTFC'), (select ID from T_PHYSIKTYP where NAME = 'ADSL-UK0 (Alc)'));
