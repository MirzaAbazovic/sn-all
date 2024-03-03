--
-- Update-Statements fuer Einfuehrung KVZ-Daten auf HVT-Standort
-- sowie Typisierung von HVTs
--


-- Erweitern von t_hvt_standort um die Spalten STANDORTTYP, KVZ_NUMMER und KVZ_SCHALTNUMMER

alter table t_hvt_standort add column KVZ_NUMMER varchar(5) after ASB;
alter table t_hvt_standort add column KVZ_SCHALTNUMMER varchar(8) after KVZ_NUMMER;
alter table t_hvt_standort add column STANDORT_TYP_REF_ID int(9) after KVZ_SCHALTNUMMER;

-- foreign key von hvt-std -> reference

ALTER TABLE t_hvt_standort
  ADD CONSTRAINT FK_HVTSTDTYP_2_REF
      FOREIGN KEY (STANDORT_TYP_REF_ID)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

-- insert into t_reference...

insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11000, 'STANDORT_TYP', 'HVT', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11001, 'STANDORT_TYP', 'KVZ', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11002, 'STANDORT_TYP', 'FTTB', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (11003, 'STANDORT_TYP', 'EWSD', true);

-- STANDORT_TYP_REF_ID in bestehenden Datensätzen setzen, zuerst die HVTs

update t_hvt_standort, t_hvt_gruppe set t_hvt_standort.standort_typ_ref_id=11000 
where ((t_hvt_standort.hvt_gruppe_id = t_hvt_gruppe.hvt_gruppe_id) and 
(t_hvt_gruppe.ortsteil like 'HVT%'));

-- STANDORT_TYP_REF_ID in bestehenden Datensätzen setzen, die FTTBs

update t_hvt_standort, t_hvt_gruppe set t_hvt_standort.standort_typ_ref_id=11002 
where ((t_hvt_standort.hvt_gruppe_id = t_hvt_gruppe.hvt_gruppe_id) and 
(t_hvt_gruppe.ortsteil like 'M-%'));

-- STANDORT_TYP_REF_ID in bestehenden Datensätzen setzen, die KVZs

update t_hvt_standort, t_hvt_gruppe set t_hvt_standort.standort_typ_ref_id=11001 
where ((t_hvt_standort.hvt_gruppe_id = t_hvt_gruppe.hvt_gruppe_id) and 
(t_hvt_gruppe.ortsteil like 'KVZ%'));

-- STANDORT_TYP_REF_ID in bestehenden Datensätzen setzen, die EWSDs

update t_hvt_standort, t_hvt_gruppe set t_hvt_standort.standort_typ_ref_id=11003 
where ((t_hvt_standort.hvt_gruppe_id = t_hvt_gruppe.hvt_gruppe_id) and 
(t_hvt_gruppe.ortsteil like 'Switch%'));