--
-- Legt die notwendigen BG-Typen / Physiktypen und Baugruppen fuer die V5.2 und "Mnet vor Ort"
-- Ports an.
-- Wird per SQL erledigt, da die Menge sehr uebersichtlich ist und sich die Daten in der KuP
-- nicht mehr aendern.
-- Die zugehoerigen Ports werden per "normaler" Migration uebernommen.
--

alter session set nls_date_format='yyyy-mm-dd';

-- Physiktypen
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, HW_SCHNITTSTELLE, PT_GROUP)
    values (50, 'V5.2', 'V5.2 Ports', 1, 'V5.2', 1);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, HW_SCHNITTSTELLE, PT_GROUP)
    values (51, 'MVO', 'Mnet vor Ort', 1, 'MVO', 1);

-- Baugruppentypen
insert into T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME)
  values (S_T_HW_BAUGRUPPEN_TYP_0.nextVal, 'MVO', 16, 'Mnet vor Ort', '1', 'MVO', 'EWSD_DLU');
insert into T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME)
  values (S_T_HW_BAUGRUPPEN_TYP_0.nextVal, 'V5.2', 100, 'V5.2 Ports', '1', 'V5.2', 'EWSD_DLU');

-- HW-Rack (DLU) anlegen
-- Mnet vor Ort - Mnet01
insert into T_HW_RACK (ID, RACK_TYP, ANLAGENBEZ, GERAETEBEZ, HVT_ID_STANDORT, HW_PRODUCER, GUELTIG_VON, GUELTIG_BIS)
  values (S_T_HW_RACK_0.nextVal, 'DLU', 'DLU-MVO-MUC01', '', 850, 1, '2009-12-01', '2200-01-01');
commit;
insert into T_HW_RACK_DLU (RACK_ID, DLU_NUMBER, DLU_TYPE, VERSION, SWITCH)
  values ((select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC01'), '0010', 'DLUD', 0, 'MUC01');
commit;
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-06');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-07');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-08');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-09');


-- Mnet vor Ort - Mnet02
insert into T_HW_RACK (ID, RACK_TYP, ANLAGENBEZ, GERAETEBEZ, HVT_ID_STANDORT, HW_PRODUCER, GUELTIG_VON, GUELTIG_BIS)
  values (S_T_HW_RACK_0.nextVal, 'DLU', 'DLU-MVO-MUC02', '', 850, 1, '2009-12-01', '2200-01-01');
insert into T_HW_RACK_DLU (RACK_ID, DLU_NUMBER, DLU_TYPE, VERSION, SWITCH)
  values ((select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'), '0010', 'DLUD', 0, 'MUC02');
commit;
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '00-01');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '00-02');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '00-05');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '00-06');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '00-09');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-06');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-MVO-MUC02'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='MVO'), '1', '01-07');


-- V5.2
insert into T_HW_RACK (ID, RACK_TYP, ANLAGENBEZ, GERAETEBEZ, HVT_ID_STANDORT, HW_PRODUCER, GUELTIG_VON, GUELTIG_BIS)
  values (S_T_HW_RACK_0.nextVal, 'DLU', 'DLU-V52-MUC01', '', 852, 1, '2009-12-01', '2200-01-01');
insert into T_HW_RACK_DLU (RACK_ID, DLU_NUMBER, DLU_TYPE, VERSION, SWITCH)
  values ((select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'), '6003', 'DLUD', 0, 'MUC01');
commit;
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-00');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-01');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-02');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-03');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-04');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-05');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-06');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-07');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-08');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-09');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-10');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-11');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-12');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-13');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-14');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-15');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-16');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-17');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-18');
insert into T_HW_BAUGRUPPE (ID, RACK_ID, HW_BG_TYP_ID, EINGEBAUT, MOD_NUMBER)
  values (S_T_HW_BAUGRUPPE_0.nextVal, (select ID from T_HW_RACK where ANLAGENBEZ='DLU-V52-MUC01'),
    (select ID from T_HW_BAUGRUPPEN_TYP where NAME='V5.2'), '1', '00-19');

