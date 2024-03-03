-- DWH kann nicht mit "timestamp with local time zone" umgehen. Ein casten, mit TO_DATE(), des Datentyps in der View ist nicht moeglich,
-- da sonst der Index auf changed_at durch Oracle nicht verwendet wird. Daher hier Aenderung auf Datentyp date.
-- Achtung: disable trigger notwendig, da der "default" zu einem Update der Rows fuehrt und Trigger aktiviert. 

alter table T_AUFTRAG_DATEN disable all triggers;
alter table T_AUFTRAG_DATEN add CHANGED_AT date default sysdate not null;
alter table T_AUFTRAG_DATEN enable all triggers;

alter table T_AUFTRAG_STATUS disable all triggers;
alter table T_AUFTRAG_STATUS add CHANGED_AT date default sysdate not null;
alter table T_AUFTRAG_STATUS enable all triggers;

alter table T_AUFTRAG_TECHNIK disable all triggers;
alter table T_AUFTRAG_TECHNIK add CHANGED_AT date default sysdate not null;
alter table T_AUFTRAG_TECHNIK enable all triggers;

alter table T_ENDSTELLE disable all triggers;
alter table T_ENDSTELLE add CHANGED_AT date default sysdate not null;
alter table T_ENDSTELLE enable all triggers;

alter table T_RANGIERUNG disable all triggers;
alter table T_RANGIERUNG add CHANGED_AT date default sysdate not null;
alter table T_RANGIERUNG enable all triggers;

alter table T_PHYSIKTYP disable all triggers;
alter table T_PHYSIKTYP add CHANGED_AT date default sysdate not null;
alter table T_PHYSIKTYP enable all triggers;

alter table T_AUFTRAG_2_TECH_LS disable all triggers;
alter table T_AUFTRAG_2_TECH_LS add CHANGED_AT date default sysdate not null;
alter table T_AUFTRAG_2_TECH_LS enable all triggers;

alter table T_TECH_LEISTUNG disable all triggers;
alter table T_TECH_LEISTUNG add CHANGED_AT date default sysdate not null;
alter table T_TECH_LEISTUNG enable all triggers;

alter table T_REFERENCE disable all triggers;
alter table T_REFERENCE add CHANGED_AT date default sysdate not null;
alter table T_REFERENCE enable all triggers;

alter table T_HVT_STANDORT disable all triggers;
alter table T_HVT_STANDORT add CHANGED_AT date default sysdate not null;
alter table T_HVT_STANDORT enable all triggers;

CREATE INDEX IX_AUFTRAG_DATEN_CHANGED_AT ON T_AUFTRAG_DATEN (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_AUFTRAG_STATUS_CHANGED_AT ON T_AUFTRAG_STATUS (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_AUFTRAG_TECHNIK_CHNGD_AT ON T_AUFTRAG_TECHNIK (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_ENDSTELLE_CHANGED_AT ON T_ENDSTELLE (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_RANGIERUNG_CHANGED_AT ON T_RANGIERUNG (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_PHYSIKTYP_CHANGED_AT ON T_PHYSIKTYP (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_A_2_TECH_LS_CHANGD_AT ON T_AUFTRAG_2_TECH_LS (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_TECH_LEISTUNG_CHANGED_AT ON T_TECH_LEISTUNG (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_REFERENCE_CHANGED_AT ON T_REFERENCE (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_HVT_STANDORT_CHANGED_AT ON T_HVT_STANDORT (CHANGED_AT) TABLESPACE I_HURRICAN;

CREATE OR REPLACE TRIGGER T_AUFTRAG_DATEN_changed_at BEFORE UPDATE ON T_AUFTRAG_DATEN FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_AUFTRAG_STATUS_changed_at BEFORE UPDATE ON T_AUFTRAG_STATUS FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_AUFTRAG_TECHNIK_changed_at BEFORE UPDATE ON T_AUFTRAG_TECHNIK FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_ENDSTELLE_changed_at BEFORE UPDATE ON T_ENDSTELLE FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_RANGIERUNG_changed_at BEFORE UPDATE ON T_RANGIERUNG FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_PHYSIKTYP_changed_at BEFORE UPDATE ON T_PHYSIKTYP FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_AUFTRAG_2_TECH_LS_changed_at BEFORE UPDATE ON T_AUFTRAG_2_TECH_LS FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_TECH_LEISTUNG_changed_at BEFORE UPDATE ON T_TECH_LEISTUNG FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_REFERENCE_changed_at BEFORE UPDATE ON T_REFERENCE FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_HVT_STANDORT_changed_at BEFORE UPDATE ON T_HVT_STANDORT FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
