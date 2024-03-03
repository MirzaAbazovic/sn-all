-- DWH kann nicht mit "timestamp with local time zone" umgehen. Ein casten, mit TO_DATE(), des Datentyps in der View ist nicht moeglich,
-- da sonst der Index auf changed_at durch Oracle nicht verwendet wird. Daher hier Aenderung auf Datentyp date.
-- Achtung: disable trigger notwendig, da der "default" zu einem Update der Rows fuehrt und bereits existierende Trigger anderer Datumsfelder 
-- aktivieren kann. 

alter table T_AUFTRAG_2_DSLAMPROFILE disable all triggers;
alter table T_AUFTRAG_2_DSLAMPROFILE add CHANGED_AT date default sysdate not null;
alter table T_AUFTRAG_2_DSLAMPROFILE enable all triggers;

alter table T_DSLAM_PROFILE disable all triggers;
alter table T_DSLAM_PROFILE add CHANGED_AT date default sysdate not null;
alter table T_DSLAM_PROFILE enable all triggers;

alter table T_DSLAM_PROFILE_CHANGE_REASON disable all triggers;
alter table T_DSLAM_PROFILE_CHANGE_REASON add CHANGED_AT date default sysdate not null;
alter table T_DSLAM_PROFILE_CHANGE_REASON enable all triggers;

CREATE INDEX IX_AUFT2DSLAMPROF_CHANGED_AT ON T_AUFTRAG_2_DSLAMPROFILE (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_DSLAM_PROFILE_CHANGED_AT ON T_DSLAM_PROFILE (CHANGED_AT) TABLESPACE I_HURRICAN;
CREATE INDEX IX_DSLAMPROFCHNGRSN_CHANGED_AT ON T_DSLAM_PROFILE_CHANGE_REASON (CHANGED_AT) TABLESPACE I_HURRICAN;

CREATE OR REPLACE TRIGGER T_AUFT2DSLAMPROF_changed_at BEFORE UPDATE ON T_AUFTRAG_2_DSLAMPROFILE FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_DSLAM_PROFILE_changed_at BEFORE UPDATE ON T_DSLAM_PROFILE FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
CREATE OR REPLACE TRIGGER T_DSLAMPROFCHNGRSN_changed_at BEFORE UPDATE ON T_DSLAM_PROFILE_CHANGE_REASON FOR EACH ROW
begin
:new.CHANGED_AT := sysdate;
end;
/
