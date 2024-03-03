--
-- SQL-Script, um Trigger anzulegen
--

-- Before Insert Trigger fuer die Tabelle T_DSLAM_PROFILE_CHANGE
drop trigger TRBI_DSLAM_PROFILE_CHANGE;
create trigger TRBI_DSLAM_PROFILE_CHANGE BEFORE INSERT on T_DSLAM_PROFILE_CHANGE
for each row
when (new.ID is null)
begin
 select S_T_DSLAM_PROFILE_CHANGE_0.nextval into :new.ID from dual;
end;
/
commit;

-- Before Insert Trigger fuer die Tabelle T_EQUIPMENT
drop trigger TRBI_EQUIPMENT;
create trigger TRBI_EQUIPMENT BEFORE INSERT on T_EQUIPMENT
for each row
when (new.EQ_ID is null)
begin
   -- ID und DATEW setzen
   select S_T_EQUIPMENT_0.nextval into :new.EQ_ID from dual;
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Update Trigger fuer die Tabelle T_EQUIPMENT
drop trigger TRBU_EQUIPMENT;
create trigger TRBU_EQUIPMENT BEFORE UPDATE on T_EQUIPMENT
for each row
begin
   -- DATEW setzen
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert Trigger fuer die Tabelle T_RANGIERUNG
drop trigger TRBI_RANGIERUNG;
create trigger TRBI_RANGIERUNG BEFORE INSERT on T_RANGIERUNG
for each row
when (new.RANGIER_ID is null)
begin
   -- ID und DATEW setzen
   select S_T_RANGIERUNG_0.nextval into :new.RANGIER_ID from dual;
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
   SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
   :new.LAST_CHANGE_BY := USER;
end;
/
commit;

-- Before Update Trigger fuer die Tabelle T_RANGIERUNG
drop trigger TRBU_RANGIERUNG;
create trigger TRBU_RANGIERUNG BEFORE UPDATE on T_RANGIERUNG
for each row
begin
   -- DATEW setzen
   SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
   SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
   :new.LAST_CHANGE_BY := USER;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_SDH_PHYSIK
drop trigger TRBIU_SDH_PHYSIK;
create trigger TRBIU_SDH_PHYSIK BEFORE INSERT or UPDATE on T_SDH_PHYSIK
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_HVT_BESTELLUNG
drop trigger TRBIU_HVT_BESTELLUNG;
create trigger TRBIU_HVT_BESTELLUNG BEFORE INSERT or UPDATE on T_HVT_BESTELLUNG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_AUFTRAG_2_TECH_LS
drop trigger TRBIU_AUFTRAG2TECHLS;
create trigger TRBIU_AUFTRAG2TECHLS BEFORE INSERT or UPDATE on T_AUFTRAG_2_TECH_LS
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_PHYSIKUEBERNAHME
drop trigger TRBIU_PHYSIKUEBERNAHME;
create trigger TRBIU_PHYSIKUEBERNAHME BEFORE INSERT or UPDATE on T_PHYSIKUEBERNAHME
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_AUFTRAG_QOS
drop trigger TRBIU_AUFTRAG_QOS;
create trigger TRBIU_AUFTRAG_QOS BEFORE INSERT or UPDATE on T_AUFTRAG_QOS
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_BA_VERL_CONFIG
drop trigger TRBIU_BA_VERL_CONFIG;
create trigger TRBIU_BA_VERL_CONFIG BEFORE INSERT or UPDATE on T_BA_VERL_CONFIG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_BA_ZUSATZ
drop trigger TRBIU_BA_ZUSATZ;
create trigger TRBIU_BA_ZUSATZ BEFORE INSERT or UPDATE on T_BA_ZUSATZ
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_CARRIERBESTELLUNG
drop trigger TRBIU_CARRIERBESTELLUNG;
create trigger TRBIU_CARRIERBESTELLUNG BEFORE INSERT or UPDATE on T_CARRIERBESTELLUNG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMESTAMP FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_E_RECHNUNG_DRUCK
drop trigger TRBIU_E_RECHNUNG_DRUCK;
create trigger TRBIU_E_RECHNUNG_DRUCK BEFORE INSERT or UPDATE on T_E_RECHNUNG_DRUCK
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMESTAMP FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_EG_2_AUFTRAG
drop trigger TRBIU_EG_2_AUFTRAG;
create trigger TRBIU_EG_2_AUFTRAG BEFORE INSERT or UPDATE on T_EG_2_AUFTRAG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_LEISTUNG_DN
drop trigger TRBIU_LEISTUNG_DN;
create trigger TRBIU_LEISTUNG_DN BEFORE INSERT or UPDATE on T_LEISTUNG_DN
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.DATEW FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_CARRIER
drop trigger TRBIU_CARRIER;
create trigger TRBIU_CARRIER BEFORE INSERT or UPDATE on T_CARRIER
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_CARRIER_KENNUNG
drop trigger TRBIU_CARRIERKENNUNG;
create trigger TRBIU_CARRIERKENNUNG BEFORE INSERT or UPDATE on T_CARRIER_KENNUNG
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_CARRIER_KENNUNG
drop trigger TRBIU_CARRIERCONTACT;
create trigger TRBIU_CARRIERCONTACT BEFORE INSERT or UPDATE on T_CARRIER_CONTACT
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert/Update Trigger fuer die Tabelle T_CARRIER_MAPPING
drop trigger TRBIU_CARRIERMAPPING;
create trigger TRBIU_CARRIERMAPPING BEFORE INSERT or UPDATE on T_CARRIER_MAPPING
for each row
begin
 -- TIMEST setzen
 SELECT CURRENT_TIMESTAMP into :new.TIMEST FROM dual;
end;
/
commit;

-- Before Insert Trigger fuer die Tabelle T_IA_MAT_ENTNAHME_ARTIKEL
drop trigger TRBI_IA_MAT_ENTNAHME_ARTIKEL;
create trigger TRBI_IA_MAT_ENTNAHME_ARTIKEL BEFORE INSERT on T_IA_MAT_ENTNAHME_ARTIKEL
for each row
when (new.ID is null)
begin
 select S_T_IA_MAT_ENTNAHME_ARTIK_0.nextval into :new.ID from dual;
end;
/
commit;
